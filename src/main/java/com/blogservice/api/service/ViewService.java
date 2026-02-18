package com.blogservice.api.service;

import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.PostViewCount;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.PostResponse;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.repository.post.PostViewCountCacheRepository;
import com.blogservice.api.repository.post.PostViewCountRepository;
import com.blogservice.api.repository.post.ViewLockRepository;
import com.blogservice.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static com.blogservice.api.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ViewService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final PostViewCountCacheRepository postViewCountCacheRepository;
    private final ViewLockRepository viewLockRepository;

    private static final int BACK_UP_BATCH_SIZE = 100;
    private static final Duration VIEW_CACHE_TTL = Duration.ofMinutes(10);
    private final PostViewCountBackUpService postViewCountBackUpService;

    @Transactional(readOnly = true)
    public PostResponse.Views getViewCounts(Long postId) {
        Post findPost = findPostById(postId);

        verifyPostDeleted(findPost);

        Long viewCount = postViewCountCacheRepository.getViewCount(postId);

        return PostResponse.Views.builder()
                .views(viewCount)
                .build();
    }

    public void checkViewCountIncrease(Long userId, Long postId) {
        Post findPost = findPostById(postId);
        if (!viewLockRepository.setViewLock(postId, userId, VIEW_CACHE_TTL)) {
            return;
        }

        Long viewCount = postViewCountCacheRepository.increase(postId);
        if (viewCount % BACK_UP_BATCH_SIZE == 0) {
            postViewCountBackUpService.backUp(findPost, viewCount);
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ServiceException(USER_NOT_FOUND));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ServiceException(POST_NOT_FOUND));
    }

    private static void verifyPostDeleted(Post post) {
        if (post.isDeleted()) {
            throw new ServiceException(POST_DELETED);
        }
    }
}

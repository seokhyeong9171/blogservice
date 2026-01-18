package com.blogservice.api.service;

import com.blogservice.api.domain.post.Likes;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.PostCreate;
import com.blogservice.api.dto.PostEdit;
import com.blogservice.api.dto.request.post.PostSearch;
import com.blogservice.api.dto.PostResponse;
import com.blogservice.api.exception.PostNotFound;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.post.LikeRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.repository.post.ViewRepository;
import com.blogservice.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.blogservice.api.exception.ErrorCode.*;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final ViewRepository viewRepository;

    public PostCreate.Response write(Long userId, PostCreate.Request request) {
        User user = findUserById(userId);

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .isDeleted(false)
                .build();

        Post savedPost = postRepository.save(post);

        return PostCreate.Response.builder()
                .postId(savedPost.getId())
                .build();
    }

    public PostEdit.Response edit(Long userId, Long postId, PostEdit.Request request) {
        Post findPost = findPostById(postId);

        if (!Objects.equals(findPost.getUser().getId(), userId)) {
            throw new ServiceException(POST_AUTHOR_NOT_MATCHING);
        }

        findPost.edit(request);
        return PostEdit.Response.builder()
                .postId(postId)
                .build();
    }

    @Transactional(readOnly = true)
    public PostResponse.Details getDetails(Long postId) {
        Post post = findPostById(postId);

        verifyPostDeleted(post);

        return PostResponse.Details.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .writeDt(post.getCreatedAt())
                .author(PostResponse.Details.Author.of(post))
                .build();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getList(PostSearch postSearch) {
        return postRepository.getList(postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponse.VIEWS getViewCounts(Long postId) {
        Post findPost = findPostById(postId);

        verifyPostDeleted(findPost);

        Long viewCount = viewRepository.countByPost(findPost);
        return PostResponse.VIEWS.builder()
                .views(viewCount)
                .build();
    }

    @Transactional(readOnly = true)
    public PostResponse.LIKES getLikeCounts(Long postId) {
        Post findPost = findPostById(postId);

        verifyPostDeleted(findPost);

        Long likeCount = likeRepository.countByPost(findPost);
        return PostResponse.LIKES.builder()
                .likes(likeCount)
                .build();
    }

    public PostResponse.LIKES likePost(Long userId, Long postId) {
        User findUser = findUserById(userId);
        Post findPost = findPostById(postId);

        verifyPostDeleted(findPost);

        Optional<Likes> likes = likeRepository.findByUserIdAndPostId(userId, postId);
        if (likes.isPresent()) {
            likeRepository.delete(likes.get());
        } else {
            Likes like = Likes.builder()
                    .user(findUser).post(findPost)
                    .build();
            likeRepository.save(like);
        }
        return PostResponse.LIKES.builder()
                .likes(likeRepository.countByPost(findPost))
                .build();
    }

    public void delete(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);

        postRepository.delete(post);
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

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ServiceException(USER_NOT_FOUND));
    }
}

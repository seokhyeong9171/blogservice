package com.blogservice.api.service;

import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.PostLikeCount;
import com.blogservice.api.repository.post.LikeRepository;
import com.blogservice.api.repository.post.PostLikeCountRepository;
import com.blogservice.api.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
//@Transactional
public class TestService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final PostLikeCountRepository postLikeCountRepository;

    @Transactional(timeout = 600)
    public void initPostLikeCount(long i) {
//            try {
//                Post post = postRepository.findById(i).get();
//                long count = likeRepository.countByPost(post);
//                PostLikeCount postLikeCount = PostLikeCount.create(i, count);
//                postLikeCountRepository.save(postLikeCount);
//            } catch (Exception e) {
//                log.error("postId={}", i, e);
//            }
    }
}

package com.blogservice.api.service;

import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.PostViewCount;
import com.blogservice.api.repository.post.PostViewCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostViewCountBackUpService {

    private final PostViewCountRepository postViewCountRepository;

    public void backUp(Post post, Long viewCount) {
        PostViewCount postViewCount = postViewCountRepository
                .findByPost(post)
                .orElseGet(() -> postViewCountRepository.save(PostViewCount.create(post)));

        postViewCount.update(viewCount);
    }
}

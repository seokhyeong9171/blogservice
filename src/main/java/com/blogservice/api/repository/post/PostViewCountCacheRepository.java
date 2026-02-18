package com.blogservice.api.repository.post;

public interface PostViewCountCacheRepository {

    Long increase(Long postId);

    Long getViewCount(Long postId);
}

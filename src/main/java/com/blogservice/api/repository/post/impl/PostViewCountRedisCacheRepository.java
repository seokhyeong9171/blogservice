package com.blogservice.api.repository.post.impl;

import com.blogservice.api.repository.post.PostViewCountCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostViewCountRedisCacheRepository implements PostViewCountCacheRepository {

    private final StringRedisTemplate redisTemplate;

    // post-view-count:post:{postId}
    private static final String KEY_FORMAT = "post-view-count:post:%s";

    @Override
    public Long increase(Long postId) {
        String key = generateKey(postId);
        return redisTemplate.opsForValue().increment(key, 1);
    }

    @Override
    public Long getViewCount(Long postId) {
        String result = redisTemplate.opsForValue().get(generateKey(postId));
        return result == null ? 0 : Long.parseLong(result);
    }

    private String generateKey(Long id) {
        return String.format(KEY_FORMAT, id);
    }
}

package com.blogservice.api.repository.post.impl;

import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.repository.post.ViewLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class ViewRedisLockRepository implements ViewLockRepository {

    private final StringRedisTemplate redisTemplate;

    // view-post:post:{postId}:user:{userId}:lock
    private static final String KEY_FORMAT = "view-post:post:%s:user:%s:lock";

    @Override
    public boolean setViewLock(Long postId, Long userId, Duration ttl) {
        String key = generateKey(postId, userId);
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, "", ttl));
    }

    private String generateKey(Long postId, Long userId) {
        return String.format(KEY_FORMAT, postId, userId);
    }
}

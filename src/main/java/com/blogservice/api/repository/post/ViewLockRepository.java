package com.blogservice.api.repository.post;

import java.time.Duration;

public interface ViewLockRepository {

    boolean setViewLock(Long postId, Long userId, Duration ttl);


}

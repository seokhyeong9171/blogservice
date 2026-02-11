package com.blogservice.api.repository.post;

import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.PostLikeCount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeCountRepository extends JpaRepository<PostLikeCount, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PostLikeCount> findByPost(Post post);

    @Modifying
    @Query("update PostLikeCount p set p.count = p.count + 1 where p.postId = :postId")
    long incrementCount(@Param("postId") Long postId);

    @Modifying
    @Query("update PostLikeCount p set p.count = p.count - 1 where p.postId = :postId")
    long decrementCount(@Param("postId") Long postId);
}

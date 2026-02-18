package com.blogservice.api.repository.comment;

import com.blogservice.api.domain.comment.PostCommentCount;
import com.blogservice.api.domain.post.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostCommentCountRepository extends JpaRepository<PostCommentCount, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PostCommentCount> findByPost(Post post);

    @Modifying
    @Query("update PostCommentCount p set p.count = p.count + 1 where p.postId = :postId")
    long incrementCount(@Param("postId") Long postId);

    @Modifying
    @Query("update PostCommentCount p set p.count = p.count - 1 where p.postId = :postId")
    long decrementCount(@Param("postId") Long postId);
}

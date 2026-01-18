package com.blogservice.api.repository.post;

import com.blogservice.api.domain.post.Likes;
import com.blogservice.api.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByUserIdAndPostId(Long userId, Long postId);
    long countByPost(Post post);
}

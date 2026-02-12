package com.blogservice.api.repository.post;

import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.PostViewCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostViewCountRepository extends JpaRepository<PostViewCount, Long> {

    Optional<PostViewCount> findByPost(Post post);
}

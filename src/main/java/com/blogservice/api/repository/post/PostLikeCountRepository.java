package com.blogservice.api.repository.post;

import com.blogservice.api.domain.post.PostLikeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeCountRepository extends JpaRepository<PostLikeCount, Long> {
}

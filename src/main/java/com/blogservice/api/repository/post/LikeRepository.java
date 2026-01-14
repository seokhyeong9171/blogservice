package com.blogservice.api.repository.post;

import com.blogservice.api.domain.post.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {


}

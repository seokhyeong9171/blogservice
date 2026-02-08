package com.blogservice.api.repository.post;

import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.Views;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewRepository extends JpaRepository<Views, Long> {
    long countByPost(Post post);
}

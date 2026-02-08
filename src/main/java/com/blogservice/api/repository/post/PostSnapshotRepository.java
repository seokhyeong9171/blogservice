package com.blogservice.api.repository.post;

import com.blogservice.api.domain.post.PostSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostSnapshotRepository extends JpaRepository<PostSnapshot, Long> {


}

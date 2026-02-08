package com.blogservice.api.repository.comment;

import com.blogservice.api.domain.comment.CommentSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentSnapshotRepository extends JpaRepository<CommentSnapshot, Long> {
}

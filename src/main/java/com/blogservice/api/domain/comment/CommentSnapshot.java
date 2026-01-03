package com.blogservice.api.domain.comment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class CommentSnapshot {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(nullable = false)
    private String content;

    private boolean isDeleted;

    private final LocalDateTime wroteAt = this.comment.getWroteAt();
    private LocalDateTime updatedAt = this.getUpdatedAt();

    public CommentSnapshot(Comment comment, String content, boolean isDeleted, LocalDateTime updatedAt) {
        this.comment = comment;
        this.content = content;
        this.isDeleted = isDeleted;
        this.updatedAt = updatedAt;
    }
}

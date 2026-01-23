package com.blogservice.api.domain.comment;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

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

    @Builder
    public CommentSnapshot(Comment comment, String content, boolean isDeleted) {
        this.comment = comment;
        this.content = content;
        this.isDeleted = isDeleted;
    }

    public static CommentSnapshot fromEntity(Comment comment) {
        return CommentSnapshot.builder()
                .comment(comment)
                .content(comment.getContent())
                .isDeleted(comment.isDeleted())
                .build();
    }
}

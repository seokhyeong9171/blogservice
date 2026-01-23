package com.blogservice.api.domain.post;

import com.blogservice.api.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostSnapshot extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.ALL)
    private Post post;

    private String title;

    @Lob
    private String content;

    private Boolean isDeleted;

    @Builder
    public PostSnapshot(Post post, String title, String content, Boolean isDeleted) {
        this.post = post;
        this.title = title;
        this.content = content;
        this.isDeleted = isDeleted;
    }

    public static PostSnapshot fromEntity(Post post) {
        return PostSnapshot.builder()
                .post(post)
                .title(post.getTitle())
                .content(post.getContent())
                .isDeleted(post.isDeleted())
                .build();
    }
}

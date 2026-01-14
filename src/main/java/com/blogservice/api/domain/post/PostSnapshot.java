package com.blogservice.api.domain.post;

import com.blogservice.api.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;

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

    private LocalDateTime wroteAt;

    public PostSnapshot(Post post, String title, String content) {
        this.post = post;
        this.title = title;
        this.content = content;
    }
}

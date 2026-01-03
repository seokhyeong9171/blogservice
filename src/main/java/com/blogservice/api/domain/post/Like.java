package com.blogservice.api.domain.post;

import com.blogservice.api.domain.BaseTimeEntity;
import com.blogservice.api.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Like extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    private User user;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    private Post post;

    private LocalDateTime likedAt = this.getCreatedAt();

    @Builder
    public Like(User user, Post post, LocalDateTime likedAt) {
        this.user = user;
        this.post = post;
        this.likedAt = likedAt;
    }
}
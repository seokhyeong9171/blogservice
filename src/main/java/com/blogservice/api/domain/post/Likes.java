package com.blogservice.api.domain.post;

import com.blogservice.api.domain.BaseTimeEntity;
import com.blogservice.api.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Likes extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    private User user;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    private Post post;

    @Builder
    public Likes(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
package com.blogservice.api.domain.auth;

import com.blogservice.api.domain.BaseTimeEntity;
import com.blogservice.api.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String refreshToken;

    private LocalDateTime expireAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public RefreshToken(String refreshToken, LocalDateTime expireAt, User user) {
        this.refreshToken = refreshToken;
        this.expireAt = expireAt;
        this.user = user;
    }
}

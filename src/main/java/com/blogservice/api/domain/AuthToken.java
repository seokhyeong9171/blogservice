package com.blogservice.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.*;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class AuthToken extends BaseTimeEntity{

    @Id
    private String jwt;
    private String refreshToken;

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public AuthToken(String jwt, String refreshToken, User user) {
        this.jwt = jwt;
        this.refreshToken = refreshToken;
        this.user = user;
    }
}

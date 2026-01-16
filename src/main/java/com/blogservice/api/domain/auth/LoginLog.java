package com.blogservice.api.domain.auth;

import com.blogservice.api.domain.BaseTimeEntity;
import com.blogservice.api.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class LoginLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String ipAddress;

    private String userAgent;

    @Builder
    public LoginLog(User user, String ipAddress, String userAgent) {
        this.user = user;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
}

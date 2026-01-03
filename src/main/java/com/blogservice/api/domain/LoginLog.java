package com.blogservice.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class LoginLog extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String ipAddress;

    private String userAgent;

    private LocalDateTime loginAt = this.getCreatedAt();

    @Builder
    public LoginLog(User user, String ipAddress, String userAgent, LocalDateTime loginAt) {
        this.user = user;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.loginAt = loginAt;
    }
}

package com.blogservice.api.domain.user;

import com.blogservice.api.domain.*;
import com.blogservice.api.domain.auth.LoginLog;
import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.View;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Table(name = "USERS")
@NoArgsConstructor(access = PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private LocalDate birthDt;
    @Embedded
    private Address address;
    private boolean isWithdrawal;
    private LocalDateTime registered_at = this.getCreatedAt();

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<LoginLog> loginLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<View> views = new ArrayList<>();

    @Builder
    public User(
            String name, String email, String password, String phone,
            LocalDate birthDt, Address address, boolean isWithdrawal, LocalDateTime registered_at
    ) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.birthDt = birthDt;
        this.address = address;
        this.isWithdrawal = isWithdrawal;
        this.registered_at = registered_at;
    }
}

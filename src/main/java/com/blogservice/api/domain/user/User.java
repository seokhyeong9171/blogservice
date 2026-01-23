package com.blogservice.api.domain.user;

import com.blogservice.api.domain.BaseTimeEntity;
import com.blogservice.api.domain.auth.LoginLog;
import com.blogservice.api.domain.auth.RefreshToken;
import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.Views;
import com.blogservice.api.dto.UserInfo;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
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
    private String nickname;
    private String name;
    private String email;
    private String password;
    private String phone;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDt;
    @Embedded
    private Address address;
    private boolean isWithdrawal;

    @Enumerated(value = STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<LoginLog> loginLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<Views> views = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @Builder
    public User(
            String nickname, String name, String email, String password, String phone,
            LocalDate birthDt, Address address, boolean isWithdrawal, Role role
    ) {
        this.nickname = nickname;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.birthDt = birthDt;
        this.address = address;
        this.isWithdrawal = isWithdrawal;
        this.role = role;
    }

    public void update(UserInfo.Update request) {
        this.nickname = request.getNickname();
        this.birthDt = request.getBirth();
        this.phone = request.getPhone();
        this.address = request.getAddress().toEntity();
    }
}

package com.blogservice.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.yaml.snakeyaml.events.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
    private LocalDateTime regist_at = this.getCreatedAt();

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
            LocalDate birthDt, Address address, boolean isWithdrawal, LocalDateTime regist_at
    ) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.birthDt = birthDt;
        this.address = address;
        this.isWithdrawal = isWithdrawal;
        this.regist_at = regist_at;
    }
}

package com.blogservice.api.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

public class UserPrincipal extends User {

    private final Long userId;

    public UserPrincipal(com.blogservice.api.domain.User user) {
        super(user.getEmail(), user.getPassword(),
                List.of(
                        new SimpleGrantedAuthority("ROLE_USER")
                )
        );
        this.userId = user.getId();
    }

    public Long getUserId() {
        return userId;
    }
}

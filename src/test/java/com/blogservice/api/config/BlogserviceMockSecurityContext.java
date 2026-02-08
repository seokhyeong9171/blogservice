package com.blogservice.api.config;

import com.blogservice.api.domain.user.Address;
import com.blogservice.api.domain.user.Role;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BlogserviceMockSecurityContext implements WithSecurityContextFactory<BlogserviceMockUser> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SecurityContext createSecurityContext(BlogserviceMockUser annotation) {
        User user = User.builder()
                .email(annotation.email())
                .name(annotation.name())
                .nickname(annotation.nickname())
                .password(passwordEncoder.encode(annotation.password()))
                .phone(annotation.phone())
                .birthDt(LocalDate.now())
                .address(Address.builder()
                        .postal(12345)
                        .address("test address")
                        .build())
                .role(annotation.role())
                .build();
        userRepository.save(user);

        UserPrincipal principal = new UserPrincipal(user);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                principal, user.getPassword(), List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authenticationToken);

        return securityContext;
    }

    public User getCurrentUser() {
        UserPrincipal principal =
                (UserPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();
        String username = principal.getUsername();
        return userRepository.findByEmail(username).get();
    }
}

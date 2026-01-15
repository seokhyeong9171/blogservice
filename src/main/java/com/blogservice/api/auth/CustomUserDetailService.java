package com.blogservice.api.auth;

import com.blogservice.api.domain.user.User;
import com.blogservice.api.exception.ErrorCode;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.blogservice.api.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User findUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new ServiceException(USER_NOT_FOUND));
        return new org.springframework.security.core.userdetails.User(
                findUser.getEmail(),
                findUser.getPassword(),
                List.of(new SimpleGrantedAuthority(findUser.getRole().name()))
        );
    }
}

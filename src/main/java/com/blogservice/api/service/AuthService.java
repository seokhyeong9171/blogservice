package com.blogservice.api.service;

import com.blogservice.api.domain.user.Address;
import com.blogservice.api.domain.user.Role;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.exception.AlreadyExistEmailException;
import com.blogservice.api.exception.ErrorCode;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.user.UserRepository;
import com.blogservice.api.dto.Signup;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.blogservice.api.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public Long signup(Signup.Request request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new ServiceException(EMAIL_DUPLICATED);
        }

        User user = createNewUser(request);

        return userRepository.save(user).getId();
    }

    private User createNewUser(Signup.Request request) {
        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encryptedPassword)
                .phone(request.getPhone())
                .birthDt(request.getBirthDt())
                .address(Address.fromRequest(request.getAddress()))
                .isWithdrawal(false)
                .role(Role.USER)
                .build();
    }
}

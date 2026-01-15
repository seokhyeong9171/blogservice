package com.blogservice.api.service;

import com.blogservice.api.auth.JwtProvider;
import com.blogservice.api.domain.user.Address;
import com.blogservice.api.domain.user.Role;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.Login;
import com.blogservice.api.exception.AlreadyExistEmailException;
import com.blogservice.api.exception.ErrorCode;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.user.UserRepository;
import com.blogservice.api.dto.Signup;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final JwtProvider jwtProvider;

    public Long signup(Signup.Request request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new ServiceException(EMAIL_DUPLICATED);
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ServiceException(NICKNAME_DUPLICATED);
        }

        User user = createNewUser(request);

        return userRepository.save(user).getId();
    }

    private User createNewUser(Signup.Request request) {
        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        return User.builder()
                .nickname(request.getNickname())
                .name(request.getName())
                .email(request.getEmail())
                .password(encryptedPassword)
                .phone(request.getPhone())
                .birthDt(request.getBirthDt())
                .address(Address.fromRequest(request.getAddress()))
                .isWithdrawal(false)
                .role(Role.ROLE_USER)
                .build();
    }

    public String login(Login.Request request) {
        String email = request.getEmail();
        String password = request.getPassword();

        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ServiceException(USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, findUser.getPassword())) {
            throw new ServiceException(PASSWORD_NOT_MATCHING);
        }

        return jwtProvider.generateJwtToken(email);
    }
}

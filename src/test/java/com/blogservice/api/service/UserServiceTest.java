package com.blogservice.api.service;

import com.blogservice.api.auth.JwtProvider;
import com.blogservice.api.config.BlogserviceMockSecurityContext;
import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.config.UserPrincipal;
import com.blogservice.api.domain.auth.RefreshToken;
import com.blogservice.api.domain.user.Role;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.Login;
import com.blogservice.api.dto.Signup;
import com.blogservice.api.dto.UserInfo;
import com.blogservice.api.exception.ErrorCode;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.auth.RefreshTokenRepository;
import com.blogservice.api.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.blogservice.api.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private BlogserviceMockSecurityContext securityContext;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserService userService;

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공")
    @BlogserviceMockUser
    void changePassword_success() {
        // given
        UserInfo.ChangePassword request = UserInfo.ChangePassword.builder()
                .currentPassword("testpassword")
                .newPassword("newpassword")
                .build();

        // when
        userService.changePassword(getMockUser().getId(), request);

        // then
        User user = userRepository.findById(getMockUser().getId()).get();
        assertTrue(passwordEncoder.matches(request.getNewPassword(), user.getPassword()));

    }

    @Test
    @DisplayName("비밀번호 변경 - 실패 - 비밀번호 다름")
    @BlogserviceMockUser
    void changePassword_fail_wrong_cur() {
        // given
        UserInfo.ChangePassword request = UserInfo.ChangePassword.builder()
                .currentPassword("testpassword" + "aaa")
                .newPassword("newpassword")
                .build();

        // expected
        ServiceException serviceException = assertThrowsExactly
                (ServiceException.class, () -> userService.changePassword(getMockUser().getId(), request));

        assertEquals(PASSWORD_NOT_MATCHING.getMessage(), serviceException.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패 - 변경 비밀번호 이전과 동일")
    @BlogserviceMockUser
    void changePassword_same_cur() {
        // given
        UserInfo.ChangePassword request = UserInfo.ChangePassword.builder()
                .currentPassword("testpassword")
                .newPassword("testpassword")
                .build();

        // expected
        ServiceException serviceException = assertThrowsExactly
                (ServiceException.class, () -> userService.changePassword(getMockUser().getId(), request));

        assertEquals(SAME_PASSWORD.getMessage(), serviceException.getMessage());
    }

    public User getMockUser() {
        return securityContext.getCurrentUser();
    }

}
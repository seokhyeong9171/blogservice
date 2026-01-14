package com.blogservice.api.service;

import com.blogservice.api.domain.user.User;
import com.blogservice.api.exception.AlreadyExistEmailException;
import com.blogservice.api.exception.ErrorCode;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.user.UserRepository;
import com.blogservice.api.dto.Signup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static com.blogservice.api.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // given
        Signup.Request request = Signup.Request.builder()
                .name("testname")
                .email("testemail@test.com")
                .password("testpassword")
                .phone("01012345678")
                .birthDt(LocalDate.now())
                .address(Signup.Request.Address.builder()
                        .postal(12345)
                        .address("testaddress")
                        .build())
                .build();

        // when
        authService.signup(request);

        // then
        assertEquals(1, userRepository.count());
        User findUser = userRepository.findAll().getFirst();
        assertEquals(request.getEmail(), findUser.getEmail());
        assertEquals(request.getName(), findUser.getName());
        assertTrue(passwordEncoder.matches(request.getPassword(), findUser.getPassword()));
    }

    @Test
    @DisplayName("회원가입 실패_중복된 이메일")
    void signup_fail_dup_email() {
        // given
        String testemail = "testemail";

        User alreadyUser = User.builder()
                .email(testemail)
                .name("testname")
                .password("testpassword")
                .build();
        userRepository.save(alreadyUser);

        Signup.Request signup = Signup.Request.builder()
                .name("testname")
                .email(testemail)
                .password("testpassword")
                .build();

        // expected
        ServiceException exception =
                assertThrowsExactly(ServiceException.class, () -> authService.signup(signup));
        assertEquals(EMAIL_DUPLICATED.getStatus(), exception.getStatus());
        assertEquals(EMAIL_DUPLICATED.getMessage(), exception.getMessage());

    }

}
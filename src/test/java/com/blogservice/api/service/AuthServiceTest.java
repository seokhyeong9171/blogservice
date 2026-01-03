package com.blogservice.api.service;

import com.blogservice.api.domain.user.User;
import com.blogservice.api.exception.AlreadyExistEmailException;
import com.blogservice.api.repository.user.UserRepository;
import com.blogservice.api.request.Signup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    void test1() {
        // given
        Signup signup = Signup.builder()
                .name("testname")
                .email("testemail")
                .password("testpassword")
                .build();

        // when
        authService.signup(signup);

        // then
        assertEquals(1, userRepository.count());
        User findUser = userRepository.findAll().getFirst();
        assertEquals(signup.getEmail(), findUser.getEmail());
        assertEquals(signup.getName(), findUser.getName());
        assertTrue(passwordEncoder.matches(signup.getPassword(), findUser.getPassword()));
    }

    @Test
    @DisplayName("회원가입시 중복된 이메일")
    void test2() {
        // given
        String testemail = "testemail";

        User alreadyUser = User.builder()
                .email(testemail)
                .name("testname")
                .password("testpassword")
                .build();
        userRepository.save(alreadyUser);

        Signup signup = Signup.builder()
                .name("testname")
                .email(testemail)
                .password("testpassword")
                .build();

        // expected
        assertThrowsExactly(AlreadyExistEmailException.class, () -> authService.signup(signup));
    }

}
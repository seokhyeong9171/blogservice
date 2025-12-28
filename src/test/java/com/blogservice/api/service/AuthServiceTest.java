package com.blogservice.api.service;

import com.blogservice.api.domain.User;
import com.blogservice.api.exception.AlreadyExistEmailException;
import com.blogservice.api.repository.UserRepository;
import com.blogservice.api.request.Login;
import com.blogservice.api.request.Signup;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        assertEquals(signup.getPassword(), findUser.getPassword());
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
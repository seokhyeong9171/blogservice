package com.blogservice.api.controller;

import com.blogservice.api.domain.user.Role;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.Login;
import com.blogservice.api.exception.ErrorCode;
import com.blogservice.api.repository.user.UserRepository;
import com.blogservice.api.dto.Signup;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.blogservice.api.exception.ErrorCode.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clean() {
//        userRepository.deleteAll();
//        jdbcTemplate.execute("ALTER TABLE USERS ALTER COLUMN id RESTART WITH 1");
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입_성공")
    void signup_success() throws Exception {
        // given
        Signup.Request request = Signup.Request.builder()
                .nickname("nickname")
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

        // expected
        mockMvc.perform(post("/api/auth/signup")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception{
        // when
        User user = User.builder()
                .email("testemail@test.com")
                .password(passwordEncoder.encode("testpassword"))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        Login.Request request = Login.Request.builder()
                .email("testemail@test.com")
                .password("testpassword")
                .build();

        // expected
        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(header().exists(AUTHORIZATION))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.jwt").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - 이메일 오류")
    void login_fail_invalid_email() throws Exception{
        // when
        User user = User.builder()
                .email("testemail@test.com")
                .password(passwordEncoder.encode("testpassword"))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        Login.Request request = Login.Request.builder()
                .email("aaa"+ "testemail@test.com")
                .password("testpassword")
                .build();

        // expected
        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(USER_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.message").value(USER_NOT_FOUND.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 오류")
    void login_fail_invalid_password() throws Exception{
        // when
        User user = User.builder()
                .email("testemail@test.com")
                .password(passwordEncoder.encode("testpassword"))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        Login.Request request = Login.Request.builder()
                .email("testemail@test.com")
                .password("testpassword" + "aaa")
                .build();

        // expected
        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(PASSWORD_NOT_MATCHING.getStatus().value()))
                .andExpect(jsonPath("$.message").value(PASSWORD_NOT_MATCHING.getMessage()))
                .andDo(print());
    }


}
package com.blogservice.api.controller;

import com.blogservice.api.domain.Session;
import com.blogservice.api.domain.User;
import com.blogservice.api.repository.SessionRepository;
import com.blogservice.api.repository.UserRepository;
import com.blogservice.api.request.Login;
import com.blogservice.api.request.PostCreate;
import com.blogservice.api.request.Signup;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;

    @BeforeEach
    void clean() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

        jdbcTemplate.execute("TRUNCATE TABLE session");
        jdbcTemplate.execute("TRUNCATE TABLE users");

        jdbcTemplate.execute("ALTER TABLE session ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");

        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    @Test
    @DisplayName("로그인 성공.")
    void test1() throws Exception {
        // given
        String email = "admin@admin.com";
        String password = "12345";

        userRepository.save(User.builder()
                .name("admin")
                .email(email)
                .password(password)
                .build());

        Login login = Login.builder()
                .email(email)
                .password(password)
                .build();

        String json = objectMapper.writeValueAsString(login);

        // expected
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("로그인 성공 후 세션 1개 생성.")
    void test2() throws Exception {
        // given
        String email = "admin@admin.com";
        String password = "12345";

        User savedUser = userRepository.save(User.builder()
                .name("admin")
                .email(email)
                .password(password)
                .build());

        Login login = Login.builder()
                .email(email)
                .password(password)
                .build();

        String json = objectMapper.writeValueAsString(login);

        // expected
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());

        User loggedInUser = userRepository.findById(savedUser.getId()).orElseThrow(RuntimeException::new);

        assertEquals(1L, loggedInUser.getSessions().size());
    }

    @Test
    @Transactional
    @DisplayName("로그인 성공 후 세션 응답.")
    void test3() throws Exception {
        // given
        String email = "admin@admin.com";
        String password = "12345";

        User savedUser = userRepository.save(User.builder()
                .name("admin")
                .email(email)
                .password(password)
                .build());

        Login login = Login.builder()
                .email(email)
                .password(password)
                .build();

        String json = objectMapper.writeValueAsString(login);

        // expected
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", Matchers.notNullValue()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 후 권한이 필요한 페이지 접속 /test")
    void test4() throws Exception {
        // given
        String email = "admin@admin.com";
        String password = "12345";

        User user = User.builder()
                .name("admin")
                .email(email)
                .password(password)
                .build();
        Session session = user.addSession();
        User savedUser = userRepository.save(user);


        // expected
        mockMvc.perform(get("/test")
                        .header("Authorization", session.getAccessToken())
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 후 검증되지 않는 세션값으로 권한이 필요한 페이지에 접속할 수 없다.")
    void test5() throws Exception {
        // given
        String email = "admin@admin.com";
        String password = "12345";

        User user = User.builder()
                .name("admin")
                .email(email)
                .password(password)
                .build();
        Session session = user.addSession();
        User savedUser = userRepository.save(user);

        // expected
        mockMvc.perform(get("/test")
                        .header("Authorization", session.getAccessToken() + "-other")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입")
    void test6() throws Exception {
        // given
        Signup signup = Signup.builder()
                .name("testname")
                .email("testemail")
                .password("testpassword")
                .build();

        // expected
        mockMvc.perform(post("/auth/signup")
                        .content(objectMapper.writeValueAsString(signup))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }


}
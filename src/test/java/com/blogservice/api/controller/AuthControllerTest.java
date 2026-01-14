package com.blogservice.api.controller;

import com.blogservice.api.domain.user.User;
import com.blogservice.api.repository.user.UserRepository;
import com.blogservice.api.dto.Signup;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

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


}
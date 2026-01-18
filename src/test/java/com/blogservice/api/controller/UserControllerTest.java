package com.blogservice.api.controller;

import com.blogservice.api.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

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
    @DisplayName("이메일 중복 체크")
    void email_dup_check() throws Exception {
        mockMvc.perform(get("/api/user/email/exists?email=test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duplicate").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 중복 체크")
    void nickname_dup_check() throws Exception {
        mockMvc.perform(get("/api/user/nickname/exists?nickname=test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duplicate").exists())
                .andDo(print());
    }

}
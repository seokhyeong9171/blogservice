package com.blogservice.api.controller;

import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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

import java.time.LocalDate;

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

    @AfterEach
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


    @Test
    @DisplayName("유저 정보 조회")
    @BlogserviceMockUser
    void get_user_info() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testname"))
                .andExpect(jsonPath("$.nickname").value("testnickname"))
                .andExpect(jsonPath("$.email").value("testemail@test.com"))
                .andExpect(jsonPath("$.birth").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.phone").value("01012345678"))
                .andExpect(jsonPath("$.address.postal").value(12345))
                .andExpect(jsonPath("$.address.address").value("test address"))
                .andDo(print());
    }
}
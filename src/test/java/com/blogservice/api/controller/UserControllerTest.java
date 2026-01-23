package com.blogservice.api.controller;

import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.UserInfo;
import com.blogservice.api.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
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

    @Test
    @DisplayName("유저 정보 업데이트")
    @BlogserviceMockUser
    void update_user_info() throws Exception {

        UserInfo.Update request = UserInfo.Update.builder()
                .nickname("changed")
                .birth(LocalDate.now().minusYears(1))
                .phone("01000001111")
                .address(UserInfo.Address.builder()
                        .postal(54321)
                        .address("changed address")
                        .build())
                .build();

        mockMvc.perform(patch("/api/user")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        User user = userRepository.findAll().getFirst();
        assertEquals("changed", user.getNickname());
        assertEquals(LocalDate.now().minusYears(1), user.getBirthDt());
        assertEquals("01000001111", user.getPhone());
        assertEquals("changed address", user.getAddress().getAddress());
        assertEquals(54321, user.getAddress().getPostal());
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공")
    @BlogserviceMockUser
    void change_password_success() throws Exception {
        UserInfo.ChangePassword request = UserInfo.ChangePassword.builder()
                .currentPassword("testpassword")
                .newPassword("newpassword")
                .build();

        mockMvc.perform(patch("/api/user/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패 - 현재 비밀번호 다름")
    @BlogserviceMockUser
    void change_password_fail_wrong_cur() throws Exception {
        UserInfo.ChangePassword request = UserInfo.ChangePassword.builder()
                .currentPassword("testpassword" + "aaa")
                .newPassword("newpassword")
                .build();

        mockMvc.perform(patch("/api/user/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패 - 변경 비밀번호 이전과 동일")
    @BlogserviceMockUser
    void change_password_fail_same_request() throws Exception {
        UserInfo.ChangePassword request = UserInfo.ChangePassword.builder()
                .currentPassword("testpassword")
                .newPassword("testpassword")
                .build();

        mockMvc.perform(patch("/api/user/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
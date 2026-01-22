package com.blogservice.api.controller;

import com.blogservice.api.config.BlogserviceMockSecurityContext;
import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.blogservice.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
public class UserControllerDocTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("이메일 중복 조회")
    void email_dup_check() throws Exception {
        // given
        User alreadyUser = User.builder()
                .email("test@test.com")
                .name("testname")
                .password("testpassword")
                .build();
        userRepository.save(alreadyUser);

        // expected
        this.mockMvc.perform(get("/api/user/email/exists")
                        .param("email", "test@test.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-email-check", queryParameters(
                        parameterWithName("email").description("이메일")
                        ),
                        responseFields(
                                fieldWithPath("duplicate").description("중복여부")
                        )
                        ));
    }
    @Test
    @DisplayName("닉네임 중복 조회")
    void nickname_dup_check() throws Exception {
        // given
        User alreadyUser = User.builder()
                .email("test@test.com")
                .nickname("testnick")
                .name("testname")
                .password("testpassword")
                .build();
        userRepository.save(alreadyUser);

        // expected
        this.mockMvc.perform(get("/api/user/nickname/exists")
                        .param("nickname", "testnick"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-nickname-check", queryParameters(
                                parameterWithName("nickname").description("닉네임")
                        ),
                        responseFields(
                                fieldWithPath("duplicate").description("중복여부")
                        )
                ));
    }

    @Test
    @DisplayName("유저 정보 조회")
    @BlogserviceMockUser
    void get_user_info() throws Exception {
        // expected
        this.mockMvc.perform(get("/api/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-info",
                        responseFields(
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("birth").description("생년월일"),
                                fieldWithPath("phone").description("전화번호"),
                                fieldWithPath("address.postal").description("우편번호"),
                                fieldWithPath("address.address").description("상세주소")
                        )
                ));
    }

}

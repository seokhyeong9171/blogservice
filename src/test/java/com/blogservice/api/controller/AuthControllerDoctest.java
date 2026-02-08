package com.blogservice.api.controller;

import com.blogservice.api.auth.RefreshTokenProvider;
import com.blogservice.api.config.BlogserviceMockSecurityContext;
import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.domain.auth.RefreshToken;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.dto.Login;
import com.blogservice.api.dto.ReIssue;
import com.blogservice.api.dto.Signup;
import com.blogservice.api.repository.auth.RefreshTokenRepository;
import com.blogservice.api.repository.post.PostRepository;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.blogservice.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
public class AuthControllerDoctest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogserviceMockSecurityContext securityContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RefreshTokenProvider refreshTokenProvider;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("유저 회원가입")
    void user_signup() throws Exception {
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
        this.mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("user-signup",
                        requestFields(
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("name").description("이름"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("phone").description("전화번호"),
                                fieldWithPath("birthDt").description("생년월일"),
//                                fieldWithPath("address.postal").description("주소"),
                                fieldWithPath("address.postal").description("우편번호"),
                                fieldWithPath("address.address").description("주소")
                        ),
                        responseFields(
                                fieldWithPath("userId").description("유저아이디")
                        )
                ));
    }

    @Test
    @DisplayName("유저 로그인")
    @BlogserviceMockUser
    void user_login() throws Exception {
        // given
        Login.Request request = Login.Request.builder()
                .email("testemail@test.com")
                .password("testpassword")
                .build();

        // expected
        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-login",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("jwt").description("JWT token")
                        )
                ));
    }

    @Test
    @DisplayName("토큰 재발급")
    @BlogserviceMockUser
    void reissue_token() throws Exception {
        // given
        RefreshToken refreshToken = refreshTokenProvider.getRefreshToken(securityContext.getCurrentUser());
        refreshTokenRepository.save(refreshToken);

        // expected
        this.mockMvc.perform(post("/api/auth/reissue")
                        .cookie(refreshTokenProvider.getRefreshTokenCookie(refreshToken))
                        )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("token-reissue",
                        responseFields(
                                fieldWithPath("jwt").description("JWT token")
                        )
                ));

    }
}

package com.blogservice.api.service;

import com.blogservice.api.auth.JwtProvider;
import com.blogservice.api.config.BlogserviceMockSecurityContext;
import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.domain.auth.RefreshToken;
import com.blogservice.api.domain.user.Role;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.Login;
import com.blogservice.api.dto.Signup;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.auth.RefreshTokenRepository;
import com.blogservice.api.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.blogservice.api.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private BlogserviceMockSecurityContext securityContext;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
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

        // when
        authService.signup(request);

        // then
        assertEquals(1, userRepository.count());
        User findUser = userRepository.findAll().getFirst();
        assertEquals(request.getNickname(), findUser.getNickname());
        assertEquals(request.getEmail(), findUser.getEmail());
        assertEquals(request.getName(), findUser.getName());
        assertEquals(request.getPhone(), findUser.getPhone());
        assertEquals(request.getBirthDt(), findUser.getBirthDt());
        assertEquals(request.getAddress().getPostal(), findUser.getAddress().getPostal());
        assertEquals(request.getAddress().getAddress(), findUser.getAddress().getAddress());
        assertFalse(findUser.isWithdrawal());
        assertEquals(Role.ROLE_USER, findUser.getRole());
        assertTrue(passwordEncoder.matches(request.getPassword(), findUser.getPassword()));
    }

    @Test
    @DisplayName("회원가입 실패_중복된 이메일")
    void signup_fail_dup_email() {
        // given
        String testemail = "testemail";

        User alreadyUser = User.builder()
                .email(testemail)
                .name("testname")
                .password("testpassword")
                .build();
        userRepository.save(alreadyUser);

        Signup.Request signup = Signup.Request.builder()
                .name("testname")
                .email(testemail)
                .password("testpassword")
                .build();

        // expected
        ServiceException exception =
                assertThrowsExactly(ServiceException.class, () -> authService.signup(signup));
        assertEquals(EMAIL_DUPLICATED.getStatus(), exception.getStatus());
        assertEquals(EMAIL_DUPLICATED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("회원가입 실패_중복된 닉네임")
    void signup_fail_dup_nickname() {
        // given
        String testemail = "testemail";

        User alreadyUser = User.builder()
                .email(testemail)
                .name("testname")
                .nickname("testnick")
                .password("testpassword")
                .build();
        userRepository.save(alreadyUser);

        Signup.Request signup = Signup.Request.builder()
                .name("testname")
                .nickname("testnick")
                .email(testemail + 1)
                .password("testpassword")
                .build();

        // expected
        ServiceException exception =
                assertThrowsExactly(ServiceException.class, () -> authService.signup(signup));
        assertEquals(NICKNAME_DUPLICATED.getStatus(), exception.getStatus());
        assertEquals(NICKNAME_DUPLICATED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        User user = User.builder()
                .email("testemail@test.com")
                .nickname("testnick")
                .password(passwordEncoder.encode("testpassword"))
                .build();

        userRepository.save(user);

        Login.Request request = Login.Request.builder()
                .email("testemail@test.com")
                .password("testpassword")
                .build();

        // when
        Login.ResponseDto login = authService.login(request);

        // then
        assertEquals(user.getEmail(), jwtProvider.getUsername(login.getJwt()));
        assertEquals("refreshToken", login.getCookie().getName());
    }

    @Test
    @DisplayName("로그인 실패 - 해당 이메일의 유저가 없음")
    void login_fail_invalid_email() {
        // given
        User user = User.builder()
                .email("testemail@test.com")
                .nickname("testnick")
                .password(passwordEncoder.encode("testpassword"))
                .build();

        userRepository.save(user);

        Login.Request request = Login.Request.builder()
                .email("aaa" + "testemail@test.com")
                .password("testpassword")
                .build();

        // expected
        ServiceException serviceException =
                assertThrowsExactly(ServiceException.class, () -> authService.login(request));
        assertEquals(USER_NOT_FOUND.getMessage(), serviceException.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_fail_invalid_password() {
        // given
        User user = User.builder()
                .email("testemail@test.com")
                .nickname("testnick")
                .password(passwordEncoder.encode("testpassword"))
                .build();

        userRepository.save(user);

        Login.Request request = Login.Request.builder()
                .email("testemail@test.com")
                .password("testpassword" + "123")
                .build();

        // expected
        ServiceException serviceException =
                assertThrowsExactly(ServiceException.class, () -> authService.login(request));
        assertEquals(PASSWORD_NOT_MATCHING.getMessage(), serviceException.getMessage());
    }

    @Test
    @DisplayName("jwt token 재발급 성공")
    @BlogserviceMockUser
    void jwt_token_reissue_success() {
        User user = securityContext.getCurrentUser();

        // given
        String beforeJwt = jwtProvider.generateJwtToken(user.getEmail());

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .user(user)
                .expireAt(LocalDateTime.now().plusSeconds(360000))
                .build();

        refreshTokenRepository.save(refreshToken);

        // when
        String reissuedJwt = authService.reissueToken(user.getId(), refreshToken.getRefreshToken());

        // then
        assertNotEquals(beforeJwt, reissuedJwt);
        assertEquals(user.getEmail(), jwtProvider.getUsername(reissuedJwt));
    }

    @Test
    @DisplayName("jwt token 재발급 실패 - 잘못된 refresh token")
    @BlogserviceMockUser
    void jwt_token_reissue_fail_invalid_refresh_token() {
        User user = securityContext.getCurrentUser();

        // given
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .user(user)
                .expireAt(LocalDateTime.now().plusSeconds(360000))
                .build();

        refreshTokenRepository.save(refreshToken);

        // expected
        ServiceException serviceException =
                assertThrowsExactly(ServiceException.class, () -> authService.reissueToken(
                        user.getId(), refreshToken.getRefreshToken() + "aaa")
                );
        assertEquals(REFRESH_TOKEN_INVALID.getMessage(), serviceException.getMessage());
    }

    @Test
    @DisplayName("jwt token 재발급 실패 - refresh token 유효기간 초과")
    @BlogserviceMockUser
    void jwt_token_reissue_fail_refresh_token_timeout() {
        User user = securityContext.getCurrentUser();

        // given
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .user(user)
                .expireAt(LocalDateTime.now().minusSeconds(360000))
                .build();

        refreshTokenRepository.save(refreshToken);

        // expected
        ServiceException serviceException =
                assertThrowsExactly(ServiceException.class, () -> authService.reissueToken(
                        user.getId(), refreshToken.getRefreshToken())
                );
        assertEquals(REFRESH_TOKEN_INVALID.getMessage(), serviceException.getMessage());
    }

}
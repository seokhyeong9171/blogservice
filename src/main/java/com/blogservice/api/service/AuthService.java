package com.blogservice.api.service;

import com.blogservice.api.auth.JwtProvider;
import com.blogservice.api.auth.RefreshTokenProvider;
import com.blogservice.api.domain.auth.RefreshToken;
import com.blogservice.api.domain.user.Address;
import com.blogservice.api.domain.user.Role;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.Login;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.auth.RefreshTokenRepository;
import com.blogservice.api.repository.user.UserRepository;
import com.blogservice.api.dto.Signup;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.blogservice.api.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    public Long signup(Signup.Request request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new ServiceException(EMAIL_DUPLICATED);
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ServiceException(NICKNAME_DUPLICATED);
        }

        User user = createNewUser(request);

        return userRepository.save(user).getId();
    }

    public Login.ResponseDto login(Login.Request request) {
        String email = request.getEmail();
        String password = request.getPassword();

        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ServiceException(USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, findUser.getPassword())) {
            throw new ServiceException(PASSWORD_NOT_MATCHING);
        }

        String jwt = jwtProvider.generateJwtToken(email);
        RefreshToken refreshToken = refreshTokenProvider.getRefreshToken(findUser);
        refreshTokenRepository.save(refreshToken);
        Cookie refreshTokenCookie = refreshTokenProvider.getRefreshTokenCookie(refreshToken);

        return Login.ResponseDto.builder()
                .jwt(jwt)
                .cookie(refreshTokenCookie)
                .build();
    }

    public String reissueToken(Long userId, String refreshTokenFromCookie) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(USER_NOT_FOUND));

        List<RefreshToken> tokenList = refreshTokenRepository.findByUserOrderByExpireAtDesc(findUser);
        if (tokenList.isEmpty()) {
            throw new ServiceException(TOKEN_LIST_EMPTY);
        }
        RefreshToken refreshTokenFromDB = tokenList.getFirst();
        if (!refreshTokenProvider.validateRefreshToken(refreshTokenFromCookie, refreshTokenFromDB)) {
            throw new ServiceException(REFRESH_TOKEN_INVALID);
        }

        return jwtProvider.generateJwtToken(findUser.getEmail());
    }

    private User createNewUser(Signup.Request request) {
        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        return User.builder()
                .nickname(request.getNickname())
                .name(request.getName())
                .email(request.getEmail())
                .password(encryptedPassword)
                .phone(request.getPhone())
                .birthDt(request.getBirthDt())
                .address(Address.fromRequest(request.getAddress()))
                .isWithdrawal(false)
                .role(Role.ROLE_USER)
                .build();
    }
}

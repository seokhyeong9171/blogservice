package com.blogservice.api.service;

import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.UserInfo;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.blogservice.api.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean checkNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Transactional(readOnly = true)
    public UserInfo.Response getUserInfo(Long userId) {
        User findUser = findUserById(userId);

        return UserInfo.Response.fromEntity(findUser);
    }

    public void updateUserInfo(Long userId, UserInfo.Update request) {
        User findUser = findUserById(userId);
        findUser.update(request);

    }

    public void changePassword(Long userId, UserInfo.ChangePassword request) {
        User findUser = findUserById(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), findUser.getPassword())) {
            throw new ServiceException(PASSWORD_NOT_MATCHING);
        }

        if (passwordEncoder.matches(request.getNewPassword(), findUser.getPassword())) {
            throw new ServiceException(SAME_PASSWORD);
        }

        findUser.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ServiceException(USER_NOT_FOUND));
    }
}

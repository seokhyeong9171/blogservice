package com.blogservice.api.service;

import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.UserInfo;
import com.blogservice.api.exception.ErrorCode;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.blogservice.api.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean checkNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public UserInfo.Response getUserInfo(Long userId) {
        User findUser = findUserById(userId);

        return UserInfo.Response.fromEntity(findUser);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ServiceException(USER_NOT_FOUND));
    }
}

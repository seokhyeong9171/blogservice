package com.blogservice.api.service;

import com.blogservice.api.domain.Session;
import com.blogservice.api.domain.User;
import com.blogservice.api.exception.AlreadyExistEmailException;
import com.blogservice.api.exception.InvalidSigninInformation;
import com.blogservice.api.repository.UserRepository;
import com.blogservice.api.request.Login;
import com.blogservice.api.request.Signup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public Long signin(Login login) {

        User user = userRepository.findByEmailAndPassword(login.getEmail(), login.getPassword())
                .orElseThrow(InvalidSigninInformation::new);

        return user.getId();
    }

    public void signup(Signup signup) {
        if(userRepository.existsByEmail(signup.getEmail())) {
            throw new AlreadyExistEmailException();
        };

        User user = User.builder()
                .email(signup.getEmail())
                .name(signup.getName())
                .password(signup.getPassword())
                .build();
        userRepository.save(user);
    }
}

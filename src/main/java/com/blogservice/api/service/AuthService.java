package com.blogservice.api.service;

import com.blogservice.api.domain.Session;
import com.blogservice.api.domain.User;
import com.blogservice.api.exception.AlreadyExistEmailException;
import com.blogservice.api.exception.InvalidSigninInformation;
import com.blogservice.api.repository.UserRepository;
import com.blogservice.api.request.Login;
import com.blogservice.api.request.Signup;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public Long signin(Login login) {
        User findUser = userRepository.findByEmail(login.getEmail())
                .orElseThrow(InvalidSigninInformation::new);

        boolean isMatch = passwordEncoder.matches(login.getPassword(), findUser.getPassword());
        if (!isMatch) {
            throw new InvalidSigninInformation();
        }

        return findUser.getId();
    }

    public void signup(Signup signup) {
        if(userRepository.existsByEmail(signup.getEmail())) {
            throw new AlreadyExistEmailException();
        }

        String encryptedPassword = passwordEncoder.encode(signup.getPassword());

        User user = User.builder()
                .email(signup.getEmail())
                .name(signup.getName())
                .password(encryptedPassword)
                .build();

        userRepository.save(user);
    }
}

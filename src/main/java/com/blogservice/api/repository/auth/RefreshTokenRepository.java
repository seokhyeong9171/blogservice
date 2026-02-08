package com.blogservice.api.repository.auth;

import com.blogservice.api.domain.auth.RefreshToken;
import com.blogservice.api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    List<RefreshToken> findByUserOrderByExpireAtDesc(User user);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}

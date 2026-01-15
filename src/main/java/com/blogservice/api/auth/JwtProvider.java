package com.blogservice.api.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final SecretKey secretKey;
    @Value("${jwt.expire}")
    private Long jwtExpireMs;

    public String generateJwtToken(String username) {
        // todo
        //  1. jwt 생성 클래스 분리
        //  2. jwt 만료 시간 등 설정
        return Jwts.builder()
                .claim("username", username)
                .signWith(secretKey)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpireMs))
                .compact();
    }

    public String getUsername(String jwt) {
        Jws<Claims> claims = parseJwt(jwt);
        return claims.getPayload().get("username", String.class);
    }

    public Jws<Claims> parseJwt(String jwt) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwt);

        return claims;
    }

    public boolean verifyJwtToken(String jwt) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwt);

        return claims.getPayload().getIssuedAt().before(new Date())
                && claims.getPayload().getExpiration().after(new Date());
    }


}

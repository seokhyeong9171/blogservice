package com.blogservice.api.auth;

import com.blogservice.api.exception.Unauthorized;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final SecretKey secretKey;

    public String generateJwtToken(Long userId) {
        // todo
        //  1. jwt 생성 클래스 분리
        //  2. jwt 만료 시간 등 설정
        return Jwts.builder()
                .claim("userId", userId)
                .signWith(secretKey)
                .issuedAt(new Date())
                // todo
                //  expiredAt 추가
                .compact();
    }

    public Jws<Claims> parseJwt(String jws) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jws);

        if (!verifyJwtToken(claims)) {
            throw new Unauthorized();
        }

        return claims;
    }

    private boolean verifyJwtToken(Jws<Claims> claims) {
        // todo
        //  jwt 만료일 이용한 검증 추가
        return claims.getPayload().getIssuedAt().before(new Date());
    }


}

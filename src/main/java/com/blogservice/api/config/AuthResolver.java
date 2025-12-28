package com.blogservice.api.config;

import com.blogservice.api.config.data.UserSession;
import com.blogservice.api.domain.Session;
import com.blogservice.api.exception.Unauthorized;
import com.blogservice.api.repository.SessionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryCustomizer;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.crypto.SecretKey;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthResolver implements HandlerMethodArgumentResolver {

    private final SessionRepository sessionRepository;
    private final SecretKey secretKey;
    private final ServletWebServerFactoryCustomizer servletWebServerFactoryCustomizer;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserSession.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String jws = webRequest.getHeader("Authorization");
        if (jws == null || jws.isEmpty()) {
            log.error("servletRequest is null");
            throw new Unauthorized();
        }

        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build().
                    parseSignedClaims(jws);

            String userId = claims.getPayload().getSubject();
            return new UserSession(Long.parseLong(userId));

        } catch (JwtException e) {
            throw new Unauthorized();
        }
    }
}

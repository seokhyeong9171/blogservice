package com.blogservice.api.config;

import com.blogservice.api.auth.JwtProvider;
import com.blogservice.api.config.data.UserSession;
import com.blogservice.api.exception.Unauthorized;
import com.blogservice.api.repository.SessionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthResolver implements HandlerMethodArgumentResolver {

    private final SessionRepository sessionRepository;
    private final JwtProvider jwtProvider;

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
            Jws<Claims> claims = jwtProvider.parseJwt(jws);
            Long userId = claims.getPayload().get("userId", Long.class);
            return new UserSession(userId);

        } catch (JwtException e) {
            throw new Unauthorized();
        }
    }
}

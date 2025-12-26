package com.blogservice.api.config;

import com.blogservice.api.config.data.UserSession;
import com.blogservice.api.exception.Unauthorized;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserSession.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String accessToken = webRequest.getParameter("accessToken");
        if (accessToken == null || accessToken.isEmpty()) {
            throw new Unauthorized();
        }
        UserSession userSession = new UserSession();
        userSession.name = accessToken;
        return userSession;
    }
}

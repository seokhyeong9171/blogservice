package com.blogservice.api.config;

import com.blogservice.api.domain.user.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = BlogserviceMockSecurityContext.class)
public @interface BlogserviceMockUser {

    String email() default "testemail@test.com";
    String name() default "testname";
    String password() default "testpassword";
    Role role() default Role.ROLE_USER;
}

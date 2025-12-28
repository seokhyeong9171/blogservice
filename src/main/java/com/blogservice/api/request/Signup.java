package com.blogservice.api.request;

import lombok.Builder;
import lombok.Data;

@Data
public class Signup {

    private String name;
    private String email;
    private String password;

    @Builder
    public Signup(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}

package com.blogservice.api.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Signup {

    private String name;
    private String email;
    private String password;
    private String phone;
    private LocalDate birthDt;

    private Address address;


    private static class Address {
        private Integer postal;
        private String address;
    }
}

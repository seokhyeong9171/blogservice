package com.blogservice.api.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Signup {

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;
    @Email(message = "올바른 이메일 형식으로 입력해 주세요.")
    private String email;
    @NotBlank
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하여야 합니다.")
    private String password;
    @Pattern(regexp = "^01[016789]\\d{7,8}$\n", message = "올바른 휴대폰 번호 형식으로 입력해 주세요")
    private String phone;
    @NotBlank(message = "생년월일을 입력해 주세요.")
    private LocalDate birthDt;

    @NotBlank
    private Address address;


    @Data
    private static class Address {
        @Pattern(regexp = "^\\d{5}$\n", message = "올바른 우편번호 형식으로 입력해 주세요.")
        private Integer postal;
        @NotBlank(message = "주소를 입력해주세요.")
        private String address;
    }
}

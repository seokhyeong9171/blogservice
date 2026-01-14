package com.blogservice.api.dto;

import com.blogservice.api.domain.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

import static lombok.AccessLevel.*;

public class Signup {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Request {
        @NotBlank(message = "이름을 입력해주세요.")
        private String name;
        @Email(message = "올바른 이메일 형식으로 입력해 주세요.")
        private String email;
        @NotBlank
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하여야 합니다.")
        private String password;
        @Pattern(regexp = "^010-?\\d{4}-?\\d{4}$", message = "올바른 휴대폰 번호 형식으로 입력해 주세요")
        private String phone;
        @NotNull(message = "생년월일을 입력해 주세요.")
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate birthDt;

        @NotNull
        private Address address;


        @Getter
        @AllArgsConstructor
        @NoArgsConstructor(access = PROTECTED)
        @Builder
        public static class Address {
            @Pattern(regexp = "^\\d{5}$\n", message = "올바른 우편번호 형식으로 입력해 주세요.")
            private Integer postal;
            @NotBlank(message = "주소를 입력해주세요.")
            private String address;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Response {
        private Long userId;
    }
}

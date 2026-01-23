package com.blogservice.api.dto;

import com.blogservice.api.domain.user.Address;
import com.blogservice.api.domain.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

import static lombok.AccessLevel.*;

public class UserInfo {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Response{

        private String name;
        private String nickname;
        private String email;
        private LocalDate birth;
        private String phone;

        private Address address;

        public static Response fromEntity(User user){
            return Response.builder()
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .birth(user.getBirthDt())
                    .phone(user.getPhone())
                    .address(Address.fromEntity(user.getAddress()))
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Update {

        @Size(min = 3, max = 10, message = "닉네임은 3자 이상, 10자 이하여야 합니다.")
        private String nickname;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate birth;
        @Pattern(regexp = "^010-?\\d{4}-?\\d{4}$", message = "올바른 휴대폰 번호 형식으로 입력해 주세요")
        private String phone;
        @NotNull
        private Address address;

    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Address {
        @Pattern(regexp = "^\\d{5}$\n", message = "올바른 우편번호 형식으로 입력해 주세요.")
        private Integer postal;
        @NotBlank(message = "주소를 입력해주세요.")
        private String address;

        public static Address fromEntity(com.blogservice.api.domain.user.Address address){
            return Address.builder()
                    .postal(address.getPostal())
                    .address(address.getAddress())
                    .build();
        }

        public com.blogservice.api.domain.user.Address toEntity(){
            return com.blogservice.api.domain.user.Address.builder()
                    .postal(postal)
                    .address(address)
                    .build();
        }
    }
}

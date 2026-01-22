package com.blogservice.api.dto;

import com.blogservice.api.domain.user.Address;
import com.blogservice.api.domain.user.User;
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
    public static class Address {
        private Integer postal;
        private String address;

        public static Address fromEntity(com.blogservice.api.domain.user.Address address){
            return Address.builder()
                    .postal(address.getPostal())
                    .address(address.getAddress())
                    .build();
        }
    }
}

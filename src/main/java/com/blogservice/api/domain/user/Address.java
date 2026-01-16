package com.blogservice.api.domain.user;

import com.blogservice.api.dto.Signup;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class Address {

    private Integer postal;
    private String address;

    public static Address fromRequest(Signup.Request.Address request) {
        return Address.builder()
                .postal(request.getPostal())
                .address(request.getAddress())
                .build();
    }
}

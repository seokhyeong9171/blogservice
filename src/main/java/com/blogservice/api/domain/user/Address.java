package com.blogservice.api.domain.user;

import com.blogservice.api.dto.Signup;
import jakarta.persistence.Embeddable;
import lombok.*;

import static lombok.AccessLevel.*;

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

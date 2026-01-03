package com.blogservice.api.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Address {

    private Integer postal;
    private String address;
}

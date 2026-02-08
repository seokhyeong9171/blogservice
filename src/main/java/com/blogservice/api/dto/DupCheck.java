package com.blogservice.api.dto;

import lombok.Getter;

@Getter
public class DupCheck {

    private boolean isDuplicate;

    public DupCheck(boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
    }

    public static DupCheck of(boolean isDup) {
        return new DupCheck(isDup);
    }
}

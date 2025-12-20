package com.blogservice.api.request;

import lombok.*;

import static java.lang.Math.*;

@Builder
@Getter
@Setter
public class PostSearch {

    private static final int MAX_SIZE = 2000;

    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer size = 10;

    public long getOffset() {
        return (long) (max(page, 1) - 1) * min(size, MAX_SIZE);
    }

}



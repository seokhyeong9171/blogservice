package com.blogservice.api.util;

import lombok.NoArgsConstructor;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class PageQueryUtil {

    private static final int MAX_SIZE = 100;

    public static int getOffset(int page, int size) {
        return (max(page, 1) - 1) * getSize(size);
    }

    public static int getSize(int size) {
        return min(Math.max(size, 1), MAX_SIZE);
    }
}

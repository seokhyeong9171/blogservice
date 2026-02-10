package com.blogservice.api.util;

import com.blogservice.api.domain.post.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.blogservice.api.domain.post.QPost.post;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static lombok.AccessLevel.*;

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

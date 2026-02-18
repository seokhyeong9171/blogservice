package com.blogservice.api.repository.post.impl;

import com.blogservice.api.domain.post.Post;
import com.blogservice.api.repository.post.PostRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.blogservice.api.domain.post.QPost.post;
import static java.lang.Math.max;
import static java.lang.Math.min;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static final int MAX_SIZE = 2000;

    @Override
    public List<Post> getList(Long boardId, int page, int size) {
        return jpaQueryFactory.selectFrom(post)
                .where(post.isDeleted.eq(false).and(post.board.id.eq(boardId)))
                .limit(size)
                .offset(getOffset(page, size))
                .orderBy(post.createdAt.desc())
                .fetch();
    }

    private long getOffset(int page, int size) {
        return (long) (max(page, 1) - 1) * min(size, MAX_SIZE);
    }
}

package com.blogservice.api.repository;

import com.blogservice.api.domain.Post;
import com.blogservice.api.domain.QPost;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.blogservice.api.domain.QPost.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Post> getList(int page) {
        return jpaQueryFactory.selectFrom(post)
                .limit(10)
                .offset((long) (page - 1) * page)
                .orderBy(post.id.desc())
                .fetch();
    }
}

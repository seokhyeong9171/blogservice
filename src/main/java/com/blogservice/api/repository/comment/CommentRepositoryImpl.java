package com.blogservice.api.repository.comment;

import com.blogservice.api.dto.CommentDto;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.blogservice.api.domain.comment.QComment.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    private static final int MAX_SIZE = 2000;

    @Override
    public List<CommentDto.List> findAllParentComments(Long postId, int size, int page) {
        List<CommentDto.List> response = new ArrayList<>();

        List<Tuple> tuples = queryFactory.select(comment.id, comment.isDeleted)
                .from(comment)
                .where(comment.parentComment.isNull())
                .limit(size)
                .offset(getOffset(page, size))
                .orderBy(comment.createdAt.asc())
                .fetch();

        for (Tuple tuple : tuples) {
            Long commentId = tuple.get(comment.id);

            Integer i = queryFactory.selectOne().from(comment).where(comment.parentComment.id.eq(commentId)).fetchFirst();
            Boolean existChild = i != null;

            Boolean isDeleted = tuple.get(comment.isDeleted);

            response.add(CommentDto.List.builder()
                    .commentId(commentId)
                    .isDeleted(isDeleted)
                    .existChild(existChild)
                    .build());
        }

        return response;
    }

    @Override
    public List<CommentDto.List> findAllChildComments(Long commentId, int size, int page) {
        List<CommentDto.List> response = new ArrayList<>();

        List<Tuple> tuples = queryFactory.select(comment.id, comment.isDeleted)
                .from(comment)
                .where(comment.parentComment.id.eq(commentId))
                .limit(size)
                .offset(getOffset(page, size))
                .orderBy(comment.createdAt.asc())
                .fetch();

        for (Tuple tuple : tuples) {
            Long id = tuple.get(comment.id);
            Boolean isDeleted = tuple.get(comment.isDeleted);
            CommentDto.List c = CommentDto.List.builder()
                    .commentId(id)
                    .isDeleted(isDeleted)
                    .build();
            response.add(c);
        }

        return response;
    }

    private long getOffset(int page, int size) {
        return (long) (max(page, 1) - 1) * min(size, MAX_SIZE);
    }
}

package com.blogservice.api.repository.post;

import com.blogservice.api.domain.board.Board;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.dto.CommentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Long countPostByBoard(Board board);

    @Query(
            value = "SELECT p.* " +
                    "FROM post p " +
                    "JOIN (" +
                    "    SELECT id " +
                    "    FROM post " +
                    "    WHERE board_id = :boardId " +
                    "    ORDER BY created_at DESC " +
                    "    LIMIT :limit OFFSET :offset" +
                    ") AS temp ON p.id = temp.id",
            nativeQuery = true
    )
    List<Post> findPostList(@Param("boardId") Long boardId, @Param("offset") int offset, @Param("limit") int limit);

}

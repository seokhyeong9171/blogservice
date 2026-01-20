package com.blogservice.api.service;

import com.blogservice.api.config.BlogserviceMockSecurityContext;
import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.exception.ErrorCode;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.comment.CommentRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.dto.CommentRequest;
import com.blogservice.api.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.blogservice.api.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogserviceMockSecurityContext securityContext;

    @AfterEach
    void clean() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("댓글 작성 - 성공")
    @BlogserviceMockUser
    void write_comment_success() {
        // given
        CommentRequest.Create request = CommentRequest.Create.builder()
                .content("testcomment")
                .build();

        Post savedPost = postRepository.save(Post.builder().build());

        // when
        commentService.write(getMockUser().getId(), savedPost.getId(), request);

        // then
        assertEquals(1L, commentRepository.count());
        Comment findedComment = commentRepository.findAll().getFirst();
        assertEquals(findedComment.getContent(), request.getContent());
        assertEquals(findedComment.getPost().getId(), savedPost.getId());
        assertFalse(findedComment.isDeleted());
    }

    @Test
    @DisplayName("댓글 작성 - 실패 - 삭제된 글")
    @BlogserviceMockUser
    void write_comment_fail_post_delete() {
        // given
        CommentRequest.Create request = CommentRequest.Create.builder()
                .content("testcomment")
                .build();

        Post savedPost = postRepository.save(Post.builder().isDeleted(true).build());

        // expected
        ServiceException serviceException =
                assertThrowsExactly(
                        ServiceException.class,
                        () -> commentService.write(getMockUser().getId(), savedPost.getId(), request)
                );
        assertEquals(0L, commentRepository.count());
        assertEquals(serviceException.getMessage(), POST_DELETED.getMessage());
    }

    @Test
    @DisplayName("댓글 수정 - 성공")
    @BlogserviceMockUser
    void update_comment_success() {
        // given
        Post savedPost = postRepository.save(Post.builder().build());

        Comment comment = Comment.builder()
                .post(savedPost)
                .user(getMockUser())
                .content("수정 전 댓글 내용")
                .build();
        Comment savedComment = commentRepository.save(comment);

        CommentRequest.Update request = CommentRequest.Update.builder()
                .content("수정 후 댓글 내용")
                .build();

        // when
        commentService.update(getMockUser().getId(), savedComment.getId(), request);

        // then
        Comment findedComment = commentRepository.findAll().getFirst();
        assertEquals(1L, commentRepository.count());
        assertEquals(findedComment.getContent(), request.getContent());
    }

    @Test
    @DisplayName("댓글 수정 - 실패 - 작성자 아님")
    @BlogserviceMockUser
    void update_comment_fail_author_not_matching() {
        // given
        Post savedPost = postRepository.save(Post.builder().build());

        Comment comment = Comment.builder()
                .post(savedPost)
                .user(getMockUser())
                .content("수정 전 댓글 내용")
                .build();
        Comment savedComment = commentRepository.save(comment);

        CommentRequest.Update request = CommentRequest.Update.builder()
                .content("수정 후 댓글 내용")
                .build();

        // expected
        ServiceException serviceException =
                assertThrowsExactly(
                        ServiceException.class, () -> commentService.update
                                (getMockUser().getId() + 1, savedComment.getId(), request));
        assertEquals(1L, commentRepository.count());
        assertEquals(COMMENT_AUTHOR_NOT_MATCHING.getMessage(), serviceException.getMessage());
        Comment c = commentRepository.findAll().getFirst();
        assertNotEquals(c.getContent(), request.getContent());
    }

    @Test
    @DisplayName("댓글 수정 - 실패 - 글 삭제됨")
    @BlogserviceMockUser
    void update_comment_fail_post_deleted() {
        // given
        Post savedPost = postRepository.save(Post.builder().isDeleted(true).build());

        Comment comment = Comment.builder()
                .post(savedPost)
                .user(getMockUser())
                .content("수정 전 댓글 내용")
                .build();
        Comment savedComment = commentRepository.save(comment);

        CommentRequest.Update request = CommentRequest.Update.builder()
                .content("수정 후 댓글 내용")
                .build();

        // expected
        ServiceException serviceException =
                assertThrowsExactly(
                        ServiceException.class, () -> commentService.update
                                (getMockUser().getId(), savedComment.getId(), request));
        assertEquals(1L, commentRepository.count());
        assertEquals(POST_DELETED.getMessage(), serviceException.getMessage());
        Comment c = commentRepository.findAll().getFirst();
        assertNotEquals(c.getContent(), request.getContent());
    }

    @Test
    @DisplayName("댓글 수정 - 실패 - 댓글 삭제됨")
    @BlogserviceMockUser
    void update_comment_fail_comment_deleted() {
        // given
        Post savedPost = postRepository.save(Post.builder().build());

        Comment comment = Comment.builder()
                .isDeleted(true)
                .post(savedPost)
                .user(getMockUser())
                .content("수정 전 댓글 내용")
                .build();
        Comment savedComment = commentRepository.save(comment);

        CommentRequest.Update request = CommentRequest.Update.builder()
                .content("수정 후 댓글 내용")
                .build();

        // expected
        ServiceException serviceException =
                assertThrowsExactly(
                        ServiceException.class, () -> commentService.update
                                (getMockUser().getId(), savedComment.getId(), request));
        assertEquals(1L, commentRepository.count());
        assertEquals(COMMENT_DELETED.getMessage(), serviceException.getMessage());
        Comment c = commentRepository.findAll().getFirst();
        assertNotEquals(c.getContent(), request.getContent());
    }

    private User getMockUser() {
        return securityContext.getCurrentUser();
    }
}
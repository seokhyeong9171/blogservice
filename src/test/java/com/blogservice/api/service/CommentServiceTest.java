package com.blogservice.api.service;

import com.blogservice.api.config.BlogserviceMockSecurityContext;
import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.post.Post;
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
        commentService.write(securityContext.getCurrentUser().getId(), savedPost.getId(), request);

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
                        () -> commentService.write(securityContext.getCurrentUser().getId(), savedPost.getId(), request)
                );
        assertEquals(0L, commentRepository.count());
        assertEquals(serviceException.getMessage(), POST_DELETED.getMessage());
    }
  
}
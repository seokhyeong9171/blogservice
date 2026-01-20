package com.blogservice.api.controller;

import com.blogservice.api.config.BlogserviceMockSecurityContext;
import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.repository.user.UserRepository;
import com.blogservice.api.repository.comment.CommentRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.dto.CommentRequest;
import com.blogservice.api.dto.request.comment.CommentDelete;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BlogserviceMockSecurityContext securityContext;

    @AfterEach
    void clean() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("댓글 작성 - 성공")
    void create_comment_success() throws Exception {
        // given
        User user = User.builder()
                .name("testname")
                .email("testemail")
                .password("testpassword")
                .build();
        User savedUser = userRepository.save(user);

        Post post = Post.builder()
                .user(savedUser)
                .build();
        Post savedPost = postRepository.save(post);

        CommentRequest.Create request = CommentRequest.Create.builder()
                .content("testcomment").build();

        // expected
        mockMvc.perform(post("/api/posts/{postId}/comments", savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("댓글 작성 - 실패 - 삭제된 글")
    void create_comment_fail_post_delete() throws Exception {
        // given
        User user = User.builder()
                .name("testname")
                .email("testemail")
                .password("testpassword")
                .build();
        User savedUser = userRepository.save(user);

        Post post = Post.builder()
                .user(savedUser)
                .isDeleted(true)
                .build();
        Post savedPost = postRepository.save(post);

        CommentRequest.Create request = CommentRequest.Create.builder()
                .content("testcomment").build();

        // expected
        mockMvc.perform(post("/api/posts/{postId}/comments", savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("댓글 수정 - 성공")
    void update_comment_success() throws Exception {
        // given
        User user = User.builder()
                .name("testname")
                .email("testemail")
                .password("testpassword")
                .build();
        User savedUser = userRepository.save(user);

        Post post = Post.builder()
                .user(savedUser)
                .build();
        Post savedPost = postRepository.save(post);

        Comment comment = Comment.builder()
                .user(getMockUser())
                .post(savedPost)
                .content("수정 전 댓글 내용")
                .build();
        Comment savedComment = commentRepository.save(comment);

        CommentRequest.Update request = CommentRequest.Update.builder()
                .content("수정 후 댓글 내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/comments/{commentId}", savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        Comment c = commentRepository.findAll().getFirst();
        assertEquals(c.getContent(), request.getContent());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("댓글 수정 - 실패 - 댓글 작성자 아님")
    void update_comment_fail_author_not_matching() throws Exception {
        // given
        User user = User.builder()
                .name("testname")
                .email("testemail")
                .password("testpassword")
                .build();
        User savedUser = userRepository.save(user);

        Post post = Post.builder()
                .user(savedUser)
                .build();
        Post savedPost = postRepository.save(post);

        Comment comment = Comment.builder()
                .user(savedUser)
                .post(savedPost)
                .content("수정 전 댓글 내용")
                .build();
        Comment savedComment = commentRepository.save(comment);

        CommentRequest.Update request = CommentRequest.Update.builder()
                .content("수정 후 댓글 내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/comments/{commentId}", savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Comment c = commentRepository.findAll().getFirst();
        assertNotEquals(c.getContent(), request.getContent());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("댓글 수정 - 실패 - 게시글 삭제됨")
    void update_comment_fail_post_deleted() throws Exception {
        // given
        User user = User.builder()
                .name("testname")
                .email("testemail")
                .password("testpassword")
                .build();
        User savedUser = userRepository.save(user);

        Post post = Post.builder()
                .user(savedUser)
                .isDeleted(true)
                .build();
        Post savedPost = postRepository.save(post);

        Comment comment = Comment.builder()
                .user(getMockUser())
                .post(savedPost)
                .content("수정 전 댓글 내용")
                .build();
        Comment savedComment = commentRepository.save(comment);

        CommentRequest.Update request = CommentRequest.Update.builder()
                .content("수정 후 댓글 내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/comments/{commentId}", savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Comment c = commentRepository.findAll().getFirst();
        assertNotEquals(c.getContent(), request.getContent());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("댓글 수정 - 실패 - 댓글 삭제됨")
    void update_comment_fail_comment_deleted() throws Exception {
        // given
        User user = User.builder()
                .name("testname")
                .email("testemail")
                .password("testpassword")
                .build();
        User savedUser = userRepository.save(user);

        Post post = Post.builder()
                .user(savedUser)
                .build();
        Post savedPost = postRepository.save(post);

        Comment comment = Comment.builder()
                .user(getMockUser())
                .post(savedPost)
                .content("수정 전 댓글 내용")
                .isDeleted(true)
                .build();
        Comment savedComment = commentRepository.save(comment);

        CommentRequest.Update request = CommentRequest.Update.builder()
                .content("수정 후 댓글 내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/comments/{commentId}", savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Comment c = commentRepository.findAll().getFirst();
        assertNotEquals(c.getContent(), request.getContent());
    }


//    @Test

    @DisplayName("댓글 삭제")
    void deleteComment() throws Exception {
        // given
        User user = User.builder()
                .name("testname")
                .email("testemail")
                .password("testpassword")
                .build();
        User savedUser = userRepository.save(user);

        Post post = Post.builder()
                .title("123456789012345")
                .content("bar")
                .user(savedUser)
                .build();
        postRepository.save(post);

        String commentPassword = "123456";
        Comment comment = Comment.builder()
//                .author("author")
//                .password(passwordEncoder.encode(commentPassword)).content("testcomment")
                .build();
//        comment.setPost(post);
        commentRepository.save(comment);

        CommentDelete request = new CommentDelete(commentPassword);

        //expected
        mockMvc.perform(post("/comments/{commentId}/delete", comment.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("댓글 삭제 실패 - 잘못된 비밀번호")
    void deleteComment_fail_wrong_password() throws Exception {
        // given
        User user = User.builder()
                .name("testname")
                .email("testemail")
                .password("testpassword")
                .build();
        User savedUser = userRepository.save(user);

        Post post = Post.builder()
                .title("123456789012345")
                .content("bar")
                .user(savedUser)
                .build();
        postRepository.save(post);

        String commentPassword = "123456";
        Comment comment = Comment.builder()
//                .author("author")
//                .password(passwordEncoder.encode(commentPassword))
                .content("testcomment").build();
//        comment.setPost(post);
        commentRepository.save(comment);

        CommentDelete request = new CommentDelete(commentPassword);

        //expected
        mockMvc.perform(post("/comments/{commentId}/delete", comment.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private User getMockUser() {
        return securityContext.getCurrentUser();
    }
}
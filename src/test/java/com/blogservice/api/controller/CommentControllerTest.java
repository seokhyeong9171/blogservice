package com.blogservice.api.controller;

import com.blogservice.api.config.BlogserviceMockSecurityContext;
import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.CommentDto;
import com.blogservice.api.repository.user.UserRepository;
import com.blogservice.api.repository.comment.CommentRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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

        CommentDto.Create request = CommentDto.Create.builder()
                .content("testcomment").build();

        // expected
        mockMvc.perform(post("/api/posts/{postId}/comments", savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
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

        CommentDto.Create request = CommentDto.Create.builder()
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

        com.blogservice.api.domain.comment.Comment comment = com.blogservice.api.domain.comment.Comment.builder()
                .user(getMockUser())
                .post(savedPost)
                .content("수정 전 댓글 내용")
                .build();
        com.blogservice.api.domain.comment.Comment savedComment = commentRepository.save(comment);

        CommentDto.Update request = CommentDto.Update.builder()
                .content("수정 후 댓글 내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/comments/{commentId}", savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());

        com.blogservice.api.domain.comment.Comment c = commentRepository.findAll().getFirst();
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

        com.blogservice.api.domain.comment.Comment comment = com.blogservice.api.domain.comment.Comment.builder()
                .user(savedUser)
                .post(savedPost)
                .content("수정 전 댓글 내용")
                .build();
        com.blogservice.api.domain.comment.Comment savedComment = commentRepository.save(comment);

        CommentDto.Update request = CommentDto.Update.builder()
                .content("수정 후 댓글 내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/comments/{commentId}", savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        com.blogservice.api.domain.comment.Comment c = commentRepository.findAll().getFirst();
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

        com.blogservice.api.domain.comment.Comment comment = com.blogservice.api.domain.comment.Comment.builder()
                .user(getMockUser())
                .post(savedPost)
                .content("수정 전 댓글 내용")
                .build();
        com.blogservice.api.domain.comment.Comment savedComment = commentRepository.save(comment);

        CommentDto.Update request = CommentDto.Update.builder()
                .content("수정 후 댓글 내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/comments/{commentId}", savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        com.blogservice.api.domain.comment.Comment c = commentRepository.findAll().getFirst();
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

        com.blogservice.api.domain.comment.Comment comment = com.blogservice.api.domain.comment.Comment.builder()
                .user(getMockUser())
                .post(savedPost)
                .content("수정 전 댓글 내용")
                .isDeleted(true)
                .build();
        com.blogservice.api.domain.comment.Comment savedComment = commentRepository.save(comment);

        CommentDto.Update request = CommentDto.Update.builder()
                .content("수정 후 댓글 내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/comments/{commentId}", savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        com.blogservice.api.domain.comment.Comment c = commentRepository.findAll().getFirst();
        assertNotEquals(c.getContent(), request.getContent());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("댓글 삭제 - 성공")
    void delete_comment_success() throws Exception {
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

        com.blogservice.api.domain.comment.Comment comment = com.blogservice.api.domain.comment.Comment.builder()
                .user(getMockUser())
                .post(savedPost)
                .isDeleted(false)
                .content("수정 전 댓글 내용")
                .build();
        com.blogservice.api.domain.comment.Comment savedComment = commentRepository.save(comment);

        // expected
        mockMvc.perform(delete("/api/comments/{commentId}", savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        assertTrue(commentRepository.findAll().getFirst().isDeleted());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("댓글 삭제 - 실패 - 댓글 작성자 아님")
    void delete_comment_fail_author_not_matching() throws Exception {
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

        com.blogservice.api.domain.comment.Comment comment = com.blogservice.api.domain.comment.Comment.builder()
                .user(savedUser)
                .post(savedPost)
                .isDeleted(false)
                .content("수정 전 댓글 내용")
                .build();
        com.blogservice.api.domain.comment.Comment savedComment = commentRepository.save(comment);

        // expected
        mockMvc.perform(delete("/api/comments/{commentId}", savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());

        assertFalse(commentRepository.findAll().getFirst().isDeleted());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("댓글 삭제 - 실패 - 댓글 삭제됨")
    void delete_comment_fail_comment_deleted() throws Exception {
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

        com.blogservice.api.domain.comment.Comment comment = com.blogservice.api.domain.comment.Comment.builder()
                .user(getMockUser())
                .post(savedPost)
                .content("수정 전 댓글 내용")
                .isDeleted(true)
                .build();
        com.blogservice.api.domain.comment.Comment savedComment = commentRepository.save(comment);

        // expected
        mockMvc.perform(delete("/api/comments/{commentId}", savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());

        assertTrue(commentRepository.findAll().getFirst().isDeleted());
    }

    @Test
    @DisplayName("댓글 상세 조회 - 성공")
    void get_comment_success() throws Exception {
        // given
        Post post = Post.builder().build();
        Post savedPost = postRepository.save(post);

        User user = User.builder().nickname("testnickname").build();
        User savedUser = userRepository.save(user);

        Comment comment = Comment.builder()
                .post(savedPost)
                .user(savedUser)
                .content("get_comment_success")
                .build();
        Comment savedComment = commentRepository.save(comment);

        // expected
        mockMvc.perform(get("/api/comments/{commentId}", savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("get_comment_success"))
                .andExpect(jsonPath("$.writeDt", startsWith(savedComment.getCreatedAt().toString().substring(0, 19))))
                .andExpect(jsonPath("$.author.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.author.nickname").value(savedUser.getNickname()));
    }

    @Test
    @DisplayName("댓글 상세 조회 - 실패 - 댓글 삭제됨")
    void get_comment_fail_comment_deleted() throws Exception {
        // given
        Post post = Post.builder().build();
        Post savedPost = postRepository.save(post);

        User user = User.builder().nickname("testnickname").build();
        User savedUser = userRepository.save(user);

        Comment comment = Comment.builder()
                .post(savedPost)
                .user(savedUser)
                .isDeleted(true)
                .content("get_comment_success")
                .build();
        Comment savedComment = commentRepository.save(comment);

        // expected
        mockMvc.perform(get("/api/comments/{commentId}", savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 리스트 조회 - 성공")
    void get_comment_list_success() throws Exception {

        Post post = Post.builder().build();
        Post savedPost = postRepository.save(post);

        List<Comment> requestComments = IntStream.range(1, 31)
                .mapToObj(i -> {
                    User user = User.builder().nickname("user " + i).build();
                    User author = userRepository.save(user);
                    return Comment.builder()
                            .post(savedPost)
                            .user(author)
                            .content("내용 " + i)
                            .build();
                })
                .toList();
        commentRepository.saveAll(requestComments);

        // expected
        mockMvc.perform(get("/api/posts/{postId}/comments?page=1&size=10", savedPost.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수 조회 - 성공")
    void get_comment_count_success() throws Exception {

        Post savedPost1 = postRepository.save(Post.builder().build());
        Post savedPost2 = postRepository.save(Post.builder().build());
        User author = userRepository.save(User.builder().nickname("user").build());

        List<Comment> requestComments = IntStream.range(1, 16)
                .mapToObj(i -> Comment.builder()
                        .post(savedPost1)
                        .user(author)
                        .content("내용 " + i)
                        .build())
                .toList();
        commentRepository.saveAll(requestComments);

        commentRepository.save(
                Comment.builder()
                        .user(author).post(savedPost2).content("content")
                        .build()
        );

        // expected
        mockMvc.perform(get("/api/posts/{postId}/comments/count", savedPost1.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentCount").value(15))
                .andDo(print());
    }




    private User getMockUser() {
        return securityContext.getCurrentUser();
    }
}
package com.blogservice.api.controller;

import com.blogservice.api.config.BlogserviceMockSecurityContext;
import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.post.Likes;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.Views;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.CommentRequest;
import com.blogservice.api.dto.PostCreate;
import com.blogservice.api.dto.PostEdit;
import com.blogservice.api.repository.comment.CommentRepository;
import com.blogservice.api.repository.post.LikeRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.repository.post.ViewRepository;
import com.blogservice.api.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.blogservice.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
public class CommentControllerDocTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogserviceMockSecurityContext securityContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @AfterEach
    void clean() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글 작성")
    @BlogserviceMockUser
    void write_comment() throws Exception {
        // given
        User user = User.builder()
                .nickname("nickname")
                .build();
        User savedUser = userRepository.save(user);
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .isDeleted(false)
                .user(savedUser)
                .build();
        Post savedPost = postRepository.save(post);

        CommentRequest.Create request = CommentRequest.Create.builder()
                .content("testcontent")
                .build();

        // expected
        this.mockMvc.perform(post("/api/posts/{postId}/comments", savedPost.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("comments-create",
                        pathParameters(
                                parameterWithName("postId").description("게시글 id")
                        ),
                        requestFields(
                                fieldWithPath("content").description("내용")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 수정")
    @BlogserviceMockUser
    void update_comment() throws Exception {
        // given
        User user = User.builder()
                .nickname("nickname")
                .build();
        User savedUser = userRepository.save(user);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .isDeleted(false)
                .user(savedUser)
                .build();
        Post savedPost = postRepository.save(post);

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
        this.mockMvc.perform(patch("/api/comments/{commentId}", savedComment.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("comments-update",
                        pathParameters(
                                parameterWithName("commentId").description("댓글 id")
                        ),
                        requestFields(
                                fieldWithPath("content").description("수정 후 댓글 내용")
                        )
                ));
    }

    private User getMockUser() {
        return securityContext.getCurrentUser();
    }
}
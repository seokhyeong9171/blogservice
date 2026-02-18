package com.blogservice.api.controller;

import com.blogservice.api.config.BlogserviceMockSecurityContext;
import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.CommentDto;
import com.blogservice.api.repository.comment.CommentRepository;
import com.blogservice.api.repository.post.PostRepository;
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

        CommentDto.Create request = CommentDto.Create.builder()
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

        com.blogservice.api.domain.comment.Comment comment = com.blogservice.api.domain.comment.Comment.builder()
                .post(savedPost)
                .user(getMockUser())
                .content("수정 전 댓글 내용")
                .build();
        com.blogservice.api.domain.comment.Comment savedComment = commentRepository.save(comment);

        CommentDto.Update request = CommentDto.Update.builder()
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

    @Test
    @DisplayName("댓글 삭제")
    @BlogserviceMockUser
    void delete_comment() throws Exception {
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

        com.blogservice.api.domain.comment.Comment comment = com.blogservice.api.domain.comment.Comment.builder()
                .post(savedPost)
                .user(getMockUser())
                .content("수정 전 댓글 내용")
                .build();
        com.blogservice.api.domain.comment.Comment savedComment = commentRepository.save(comment);

        // expected
        this.mockMvc.perform(delete("/api/comments/{commentId}", savedComment.getId())
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("comments-delete",
                        pathParameters(
                                parameterWithName("commentId").description("댓글 id")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 상세 조회")
    void get_comment_details() throws Exception {
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
                .user(savedUser)
                .content("testcontent")
                .build();
        Comment savedComment = commentRepository.save(comment);

        // expected
        this.mockMvc.perform(get("/api/comments/{commentId}", savedComment.getId())
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("comments-details",
                        pathParameters(
                                parameterWithName("commentId").description("댓글 id")
                        ),
                        responseFields(
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("writeDt").description("작성일"),
                                fieldWithPath("author.id").description("작성자 아이디"),
                                fieldWithPath("author.nickname").description("작성자 닉네임"),
                                fieldWithPath("content").description("내용")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 리스트 조회")
    void get_comment_list() throws Exception {

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
                .andDo(document("comments-list",
                        pathParameters(
                                parameterWithName("postId").description("게시글 id")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("사이즈")
                        ),
                        responseFields(
                                fieldWithPath("[].commentId").description("댓글 아이디"),
                                fieldWithPath("[].isDeleted").description("삭제 여부"),
                                fieldWithPath("[].existChild").description("대댓글 존재 여부")
                        )
                ));
    }

    @Test
    @DisplayName("대댓글 리스트 조회")
//    @Transactional
    void get_child_comment_list() throws Exception {

        Post post = Post.builder().build();
        Post savedPost = postRepository.save(post);

        Comment comment = Comment.builder().content("댓글 내용").build();
        Comment savedComment = commentRepository.save(comment);

        List<Comment> requestComments = IntStream.range(1, 31)
                .mapToObj(i -> {
                    User user = User.builder().nickname("user " + i).build();
                    User author = userRepository.save(user);
                    return Comment.builder()
                            .post(savedPost)
                            .parentComment(savedComment)
                            .user(author)
                            .content("내용 " + i)
                            .build();
                })
                .toList();
        commentRepository.saveAll(requestComments);

        // expected
        mockMvc.perform(get("/api/comments/{commentId}/child?page=1&size=10", savedComment.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("comments-child-list",
                        pathParameters(
                                parameterWithName("commentId").description("댓글 id")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("사이즈")
                        ),
                        responseFields(
                                fieldWithPath("[].commentId").description("댓글 아이디"),
                                fieldWithPath("[].isDeleted").description("삭제 여부")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 수 조회")
    void get_comment_count() throws Exception {

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
                .andDo(document("comments-count",
                        pathParameters(
                                parameterWithName("postId").description("게시글 id")
                        ),
                        responseFields(
                                fieldWithPath("commentCount").description("댓글 수")
                        )
                ));
    }

    private User getMockUser() {
        return securityContext.getCurrentUser();
    }
}
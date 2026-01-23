package com.blogservice.api.controller;

import com.blogservice.api.config.BlogserviceMockSecurityContext;
import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.domain.post.Likes;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.Views;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.PostEdit;
import com.blogservice.api.repository.post.LikeRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.repository.post.PostSnapshotRepository;
import com.blogservice.api.repository.post.ViewRepository;
import com.blogservice.api.repository.user.UserRepository;
import com.blogservice.api.dto.PostCreate;
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
public class PostControllerDocTest {

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
    private ViewRepository viewRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private PostSnapshotRepository postSnapshotRepository;

    @AfterEach
    void clean() {
        postSnapshotRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("글 단건 조회")
    void view_post_details() throws Exception {
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

        // expected
        this.mockMvc.perform(get("/api/posts/{postId}", savedPost.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("post-inquiry", pathParameters(
                        parameterWithName("postId").description("게시글 ID")
                        ),
                        responseFields(
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("writeDt").description("작성일"),
                                fieldWithPath("author.id").description("작성자 아이디"),
                                fieldWithPath("author.nickname").description("작성자 닉네임")
                        )
                        ));
    }

    @Test
    @DisplayName("글 조회수 조회")
    void view_post_view_count() throws Exception {
        // given
        User user1 = User.builder()
                .nickname("testuser1")
                .email("testuser1@testuser.com")
                .password("testpassword")
                .build();
        User user2 = User.builder()
                .nickname("testuser2")
                .email("testuser2@testuser.com")
                .password("testpassword")
                .build();
        User user3 = User.builder()
                .nickname("testuser3")
                .email("testuser3@testuser.com")
                .password("testpassword")
                .build();
        userRepository.saveAll(List.of(user1, user2, user3));

        Post post = Post.builder()
                .title("testtitle")
                .content("testcontent")
                .user(user1)
                .isDeleted(false)
                .build();
        Post savedPost = postRepository.save(post);

        Views views1 = Views.builder()
                .post(savedPost)
                .user(user1)
                .build();
        Views views2 = Views.builder()
                .post(savedPost)
                .user(user2)
                .build();
        Views views3 = Views.builder()
                .post(savedPost)
                .user(user3)
                .build();
        viewRepository.saveAll(List.of(views1, views2, views3));

        // expected
        this.mockMvc.perform(get("/api/posts/{postId}/views", savedPost.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("post-view-count", pathParameters(
                                parameterWithName("postId").description("게시글 ID")
                        ),
                        responseFields(
                                fieldWithPath("views").description("게시글 조회수")
                        )
                ));
    }

    @Test
    @DisplayName("글 좋아요수 조회")
    void view_post_like_count() throws Exception {
        // given
        User user1 = User.builder()
                .nickname("testuser1")
                .email("testuser1@testuser.com")
                .password("testpassword")
                .build();
        User user2 = User.builder()
                .nickname("testuser2")
                .email("testuser2@testuser.com")
                .password("testpassword")
                .build();
        User user3 = User.builder()
                .nickname("testuser3")
                .email("testuser3@testuser.com")
                .password("testpassword")
                .build();
        userRepository.saveAll(List.of(user1, user2, user3));

        Post post = Post.builder()
                .title("testtitle")
                .content("testcontent")
                .user(user1)
                .isDeleted(false)
                .build();
        Post savedPost = postRepository.save(post);

        Likes likes1 = Likes.builder()
                .post(savedPost)
                .user(user1)
                .build();
        Likes likes2 = Likes.builder()
                .post(savedPost)
                .user(user2)
                .build();
        Likes likes3 = Likes.builder()
                .post(savedPost)
                .user(user3)
                .build();
        likeRepository.saveAll(List.of(likes1, likes2, likes3));

        // expected
        this.mockMvc.perform(get("/api/posts/{postId}/likes", savedPost.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("post-like-count", pathParameters(
                                parameterWithName("postId").description("게시글 ID")
                        ),
                        responseFields(
                                fieldWithPath("likes").description("게시글 좋아요 수")
                        )
                ));
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("글 좋아요")
    void view_post_like() throws Exception {
        // given
        User user1 = User.builder()
                .nickname("testuser1")
                .email("testuser1@testuser.com")
                .password("testpassword")
                .build();
        User user2 = User.builder()
                .nickname("testuser2")
                .email("testuser2@testuser.com")
                .password("testpassword")
                .build();
        User user3 = User.builder()
                .nickname("testuser3")
                .email("testuser3@testuser.com")
                .password("testpassword")
                .build();
        userRepository.saveAll(List.of(user1, user2, user3));

        Post post = Post.builder()
                .title("testtitle")
                .content("testcontent")
                .user(user1)
                .isDeleted(false)
                .build();
        Post savedPost = postRepository.save(post);

        Likes likes1 = Likes.builder()
                .post(savedPost)
                .user(user1)
                .build();
        Likes likes2 = Likes.builder()
                .post(savedPost)
                .user(user2)
                .build();
        Likes likes3 = Likes.builder()
                .post(savedPost)
                .user(user3)
                .build();
        likeRepository.saveAll(List.of(likes1, likes2, likes3));

        // expected
        this.mockMvc.perform(post("/api/posts/{postId}/likes", savedPost.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("post-like", pathParameters(
                                parameterWithName("postId").description("게시글 ID")
                        ),
                        responseFields(
                                fieldWithPath("likes").description("게시글 좋아요 수")
                        )
                ));
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("글 등록")
    void create_post() throws Exception {
        // given
        PostCreate.Request request = PostCreate.Request.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // expected
        this.mockMvc.perform(post("/api/posts")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("post-create",
                        requestFields(
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("내용")
                        ),
                        responseFields(
                                fieldWithPath("postId").description("게시글 아이디")
                        )
                ));
    }

    @Test
    @DisplayName("글 여러개 조회")
    void post_list() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> {
                    User user = User.builder().nickname("user " + i).build();
                    User author = userRepository.save(user);
                    return Post.builder()
                            .user(author)
                            .title("제목 " + i)
                            .content("내용 " + i)
                            .build();
                })
                .toList();
        postRepository.saveAll(requestPosts);


        // expected
        this.mockMvc.perform(get("/api/posts?page=1&size=10")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("post-list-inquiry", queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("사이즈")
                        ),
                        responseFields(
                                fieldWithPath("[].postId").description("게시글 ID"),
                                fieldWithPath("[].title").description("제목"),
                                fieldWithPath("[].views").description("조회수"),
                                fieldWithPath("[].likes").description("좋아요 수"),
                                fieldWithPath("[].author.id").description("작성자 아이디"),
                                fieldWithPath("[].author.nickname").description("작성자 닉네임")
                        )
                ));
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("글 삭제")
    void post_delete() throws Exception {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .user(securityContext.getCurrentUser())
                .build();
        Post savedPost = postRepository.save(post);

        // expected
        this.mockMvc.perform(delete("/api/posts/{postId}", savedPost.getId()).accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("post-delete", pathParameters(
                                parameterWithName("postId").description("게시글 ID")
                        )
                ));
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("글 수정")
    void post_edit() throws Exception {
        // given
        Post requestPost = Post.builder()
                .title("수정전제목").content("수정전내용")
                .user(securityContext.getCurrentUser())
                .build();
        Post savedPost = postRepository.save(requestPost);

        PostEdit.Request postEdit = PostEdit.Request.builder()
                .title("수정후내용")
                .content("수정전내용")
                .build();

        // expected
        this.mockMvc.perform(patch("/api/posts/{postId}", savedPost.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("post-edit",
                        pathParameters(
                                parameterWithName("postId").description("게시글 id")
                        ),
                        requestFields(
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("내용")
                        ),
                        responseFields(
                                fieldWithPath("postId").description("게시글 아이디")
                        )
                ));
    }

}

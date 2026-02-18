package com.blogservice.api.controller;

import com.blogservice.api.config.BlogserviceMockSecurityContext;
import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.domain.post.Likes;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.PostLikeCount;
import com.blogservice.api.domain.post.Views;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.PostCreate;
import com.blogservice.api.dto.PostEdit;
import com.blogservice.api.repository.board.BoardRepository;
import com.blogservice.api.repository.post.LikeRepository;
import com.blogservice.api.repository.post.PostLikeCountRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.repository.post.ViewRepository;
import com.blogservice.api.repository.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogserviceMockSecurityContext securityContext;

    @Autowired
    private ViewRepository viewRepository;

    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private PostLikeCountRepository postLikeCountRepository;

    @AfterEach
    void clean() {
        postLikeCountRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        viewRepository.deleteAll();
        likeRepository.deleteAll();
//        jdbcTemplate.execute("ALTER TABLE post ALTER COLUMN id RESTART WITH 1");
//        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("게시글 작성 - 성공")
    void write_post_success() throws Exception {
        // given
        PostCreate.Request request = PostCreate.Request.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/boards/{boardId}/posts", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postId").exists())
                .andDo(print());

        assertEquals(1L, postRepository.count());

        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다.", post.getTitle());
        assertEquals("내용입니다.", post.getContent());
    }

    @Test
    @DisplayName("게시글 작성 - 실패 - 인증 안됨")
    void write_post_fail_unauthorized() throws Exception {
        // given
        PostCreate.Request request = PostCreate.Request.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/boards/{boardId}/posts", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isForbidden())
                .andDo(print());

        assertEquals(0, postRepository.count());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("게시글 작성 - 실패 - 제목 없음")
    void write_post_fail_title_blank() throws Exception {
        // given
        PostCreate.Request request = PostCreate.Request.builder()
                .title("")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/boards/{boardId}/posts", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        assertEquals(0, postRepository.count());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("게시글 작성 - 실패 - 내용 없음")
    void write_post_fail_content_blank() throws Exception {
        // given
        PostCreate.Request request = PostCreate.Request.builder()
                .title("제목입니다")
                .content("")
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/boards/{boardId}/posts", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        assertEquals(0, postRepository.count());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("글 제목 수정 - 성공")
    void edit_post_title() throws Exception {
        // given
        Post requestPost = Post.builder()
                .title("수정전제목").content("수정전내용")
                .user(securityContext.getCurrentUser())
                .build();
        Post savedPost = postRepository.save(requestPost);

        PostEdit.Request request = PostEdit.Request.builder()
                .title("수정후내용")
                .content("수정전내용")
                .build();

        // expected
        mockMvc.perform(patch("/api/posts/{postId}", savedPost.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 수정 - 실패 - 인증 안됨")
    void edit_post_fail_unauthorized() throws Exception {
        // given
        PostEdit.Request request = PostEdit.Request.builder()
                .title("수정후제목")
                .content("수정후내용")
                .build();

        mockMvc.perform(patch("/api/posts/{postId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("게시글 수정 - 실패 - 제목 없음")
    void edit_post_fail_title_blank() throws Exception {
        // given
        Post requestPost = Post.builder()
                .title("수정전제목").content("수정전내용")
                .user(securityContext.getCurrentUser())
                .build();
        Post savedPost = postRepository.save(requestPost);

        PostEdit.Request request = PostEdit.Request.builder()
                .title("")
                .content("수정전내용")
                .build();

        mockMvc.perform(patch("/api/posts/{postId}", savedPost.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        assertEquals(1, postRepository.count());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("게시글 수정 - 실패 - 내용 없음")
    void edit_post_fail_content_blank() throws Exception {
        // given
        Post requestPost = Post.builder()
                .title("수정전제목").content("수정전내용")
                .user(securityContext.getCurrentUser())
                .build();
        Post savedPost = postRepository.save(requestPost);

        PostEdit.Request request = PostEdit.Request.builder()
                .title("수정전제목")
                .content("")
                .build();

        mockMvc.perform(patch("/api/posts/{postId}", savedPost.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

        assertEquals(1, postRepository.count());
    }

    @Test
    @DisplayName("글 삭제 - 성공")
    @BlogserviceMockUser
    void delete_post_success() throws Exception {
        // given
        Post post = Post.builder()
                .user(securityContext.getCurrentUser())
                .isDeleted(false)
                .build();
        Post savedPost = postRepository.save(post);

        // expected
        mockMvc.perform(delete("/api/posts/{postId}", savedPost.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        Post findPost = postRepository.findById(savedPost.getId()).get();
        assertTrue(findPost.isDeleted());
    }

    @Test
    @DisplayName("글 삭제 - 실패 - 해당 글 삭제됨")
    @BlogserviceMockUser
    void delete_post_fail_post_deleted() throws Exception {
        // given
        Post post = Post.builder()
                .user(securityContext.getCurrentUser())
                .isDeleted(true)
                .build();
        Post savedPost = postRepository.save(post);

        // expected
        mockMvc.perform(delete("/api/posts/{postId}", savedPost.getId()))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("글 삭제 - 실패 - 해당 글 존재하지 않음")
    @BlogserviceMockUser
    void delete_post_fail_post_not_found() throws Exception {

        // expected
        mockMvc.perform(delete("/api/posts/{postId}", 999L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("글 상세 조회 - 성공")
    void view_post_details_success() throws Exception {
        // given
        User user = User.builder()
                .nickname("testuser")
                .email("testuser@testuser.com")
                .password("testpassword")
                .build();
        User author = userRepository.save(user);

        Post post = Post.builder()
                .title("testtitle")
                .content("testcontent")
                .user(author)
                .isDeleted(false)
                .build();
        Post savedPost = postRepository.save(post);

        // expected
        mockMvc.perform(get("/api/posts/{postId}", savedPost.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(savedPost.getTitle()))
                .andExpect(jsonPath("$.content").value(savedPost.getContent()))
                .andExpect(jsonPath("$.writeDt").value(savedPost.getCreatedAt().toString()))
                .andExpect(jsonPath("$.author.id").value(author.getId()))
                .andExpect(jsonPath("$.author.nickname").value(author.getNickname()))
                .andDo(print());
    }

    @Test
    @DisplayName("글 상세 조회 - 실패 - 해당 글 존재하지 않음")
    void view_post_details_fail_post_not_found() throws Exception {

        // expected
        mockMvc.perform(get("/api/posts/{postId}", 999L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("글 상세 조회 - 실패 - 해당 글 삭제됨")
    void view_post_details_fail_post_deleted() throws Exception {
        // given
        User user = User.builder().build();
        User author = userRepository.save(user);

        Post post = Post.builder()
                .title("testtitle")
                .content("testcontent")
                .user(author)
                .isDeleted(true)
                .build();
        Post savedPost = postRepository.save(post);

        // expected
        mockMvc.perform(get("/api/posts/{postId}", savedPost.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("글 리스트 조회")
    void view_post_list() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> {
                    User user = User.builder().nickname("user " + i).build();
                    User author = userRepository.save(user);
                    return Post.builder()
                            .user(author)
                            .board(boardRepository.findById(1L).get())
                            .title("제목 " + i)
                            .content("내용 " + i)
                            .build();
                })
                .toList();
        postRepository.saveAll(requestPosts);


        // expected
        mockMvc.perform(get("/api/boards/{boardId}/posts?page=1&size=10", 1L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].title").value("제목 30"))
                .andExpect(jsonPath("$[0].author.nickname").value("user 30"))
                .andExpect(jsonPath("$[9].title").value("제목 21"))
                .andExpect(jsonPath("$[9].author.nickname").value("user 21"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 조회수 조회 - 성공")
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
        mockMvc.perform(get("/api/posts/{postId}/views", savedPost.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.views").value(3))
                .andDo(print());
    }

    @Test
    @DisplayName("글 조회수 조회 - 실패 - 해당 글 존재하지 않음")
    void view_post_view_count_fail_post_not_found() throws Exception {

        // expected
        mockMvc.perform(get("/api/posts/{postId}/views", 999L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("글 조회수 조회 - 실패 - 해당 글 삭제됨")
    void view_post_view_count_fail_post_deleted() throws Exception {
        // given
        User user = User.builder().build();
        User author = userRepository.save(user);

        Post post = Post.builder()
                .title("testtitle")
                .content("testcontent")
                .user(author)
                .isDeleted(true)
                .build();
        Post savedPost = postRepository.save(post);

        // expected
        mockMvc.perform(get("/api/posts/{postId}/views", savedPost.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("글 좋아요 수 조회 - 성공")
    void view_post_like_count() throws Exception {
        // given
        User user1 = User.builder()
                .nickname("testuser1")
                .email("testuser1@testuser.com")
                .password("testpassword")
                .build();
        userRepository.save(user1);

        Post post = Post.builder()
                .title("testtitle")
                .content("testcontent")
                .user(user1)
                .isDeleted(false)
                .build();
        Post savedPost = postRepository.save(post);

        PostLikeCount postLikeCount = PostLikeCount.builder()
                .post(savedPost)
                .count(3L)
                .build();
        postLikeCountRepository.save(postLikeCount);

        // expected
        mockMvc.perform(get("/api/posts/{postId}/likes", savedPost.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").value(3))
                .andDo(print());
    }

    @Test
    @DisplayName("글 좋아요 수 조회 - 실패 - 해당 글 존재하지 않음")
    void view_post_like_count_fail_post_not_found() throws Exception {

        // expected
        mockMvc.perform(get("/api/posts/{postId}/likes", 999L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("글 좋아요 수 조회 - 실패 - 해당 글 삭제됨")
    void view_post_like_count_fail_post_deleted() throws Exception {
        // given
        User user = User.builder().build();
        User author = userRepository.save(user);

        Post post = Post.builder()
                .title("testtitle")
                .content("testcontent")
                .user(author)
                .isDeleted(true)
                .build();
        Post savedPost = postRepository.save(post);

        // expected
        mockMvc.perform(get("/api/posts/{postId}/likes", savedPost.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 좋아요 - 좋아요 - 성공")
    @Transactional
    @BlogserviceMockUser
    void post_like_success() throws Exception {
        User user = User.builder().build();
        User author = userRepository.save(user);

        Post post = Post.builder().user(author).build();
        Post savedPost = postRepository.save(post);

        PostLikeCount postLikeCount = PostLikeCount.create(savedPost);
        postLikeCountRepository.save(postLikeCount);

        mockMvc.perform(post("/api/posts/{postId}/likes", savedPost.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 좋아요 - 좋아요 취소 - 성공")
    @Transactional
    @BlogserviceMockUser
    void post_unlike_success() throws Exception {
        User user = User.builder().build();
        User author = userRepository.save(user);

        Post post = Post.builder().user(author).build();
        Post savedPost = postRepository.save(post);

        Likes likes = Likes.builder().post(savedPost).user(securityContext.getCurrentUser()).build();
        likeRepository.save(likes);

        PostLikeCount postLikeCount = PostLikeCount.builder()
                .post(savedPost)
                .count(1L)
                .build();
        postLikeCountRepository.save(postLikeCount);

        mockMvc.perform(post("/api/posts/{postId}/likes", savedPost.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").value(0))
                .andDo(print());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("글 좋아요 수 조회 - 실패 - 해당 글 존재하지 않음")
    void post_like_fail_post_not_found() throws Exception {

        // expected
        mockMvc.perform(post("/api/posts/{postId}/likes", 999L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @BlogserviceMockUser
    @Transactional
    @DisplayName("글 좋아요 - 실패 - 해당 글 삭제됨")
    void post_like_fail_post_deleted() throws Exception {
        // given
        User user = User.builder().build();
        User author = userRepository.save(user);

        Post post = Post.builder()
                .title("testtitle")
                .content("testcontent")
                .user(author)
                .isDeleted(true)
                .build();
        Post savedPost = postRepository.save(post);

        // expected
        mockMvc.perform(post("/api/posts/{postId}/likes", savedPost.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

}
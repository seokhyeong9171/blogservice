package com.blogservice.api.service;

import com.blogservice.api.config.BlogserviceMockSecurityContext;
import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.domain.post.Likes;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.Views;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.PostEdit;
import com.blogservice.api.exception.PostNotFound;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.post.LikeRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.repository.post.ViewRepository;
import com.blogservice.api.repository.user.UserRepository;
import com.blogservice.api.dto.PostCreate;
import com.blogservice.api.dto.request.post.PostSearch;
import com.blogservice.api.dto.PostResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

import static com.blogservice.api.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

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

    @AfterEach
    void clean() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성 - 성공")
    @BlogserviceMockUser
    void write_post_success() {
        // given
        PostCreate.Request request = PostCreate.Request.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // when
        postService.write(securityContext.getCurrentUser().getId(), request);

        // then
        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().getFirst();
        assertEquals("제목입니다.", post.getTitle());
        assertEquals("내용입니다.", post.getContent());
        assertEquals(securityContext.getCurrentUser().getId(), post.getUserId());
        assertFalse(post.isDeleted());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("글 작성 - 실패 - 해당 유저 없음")
    void write_post_fail_user_not_found() {
        // given
        PostCreate.Request request = PostCreate.Request.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // expected
        ServiceException serviceException = assertThrowsExactly(
                ServiceException.class, () -> postService.write(securityContext.getCurrentUser().getId() + 1, request)
        );
        assertEquals(USER_NOT_FOUND.getMessage(), serviceException.getMessage());
        assertEquals(0, postRepository.count());
    }

    @Test
    @DisplayName("게시글 수정 성공")
    @BlogserviceMockUser
    void edit_post_success() {
        // given
        Post requestPost = Post.builder()
                .title("수정전제목").content("수정전내용")
                .user(securityContext.getCurrentUser())
                .build();
        Post savedPost = postRepository.save(requestPost);

        PostEdit.Request request = PostEdit.Request.builder()
                .title("수정후내용")
                .content("수정후내용")
                .build();

        // when
        PostEdit.Response response =
                postService.edit(securityContext.getCurrentUser().getId(), savedPost.getId(), request);
        assertEquals(1L, postRepository.count());
        assertEquals(savedPost.getId(), response.getPostId());
        Post post = postRepository.findById(savedPost.getId()).get();
        assertEquals(request.getTitle(), post.getTitle());
        assertEquals(request.getContent(), post.getContent());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("글 수정 - 실패 - 해당 게시글 없음")
    void update_post_fail_user_not_found() {
        // given
        Post requestPost = Post.builder()
                .title("수정전제목").content("수정전내용")
                .user(securityContext.getCurrentUser())
                .build();
        Post savedPost = postRepository.save(requestPost);

        PostEdit.Request request = PostEdit.Request.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // expected
        ServiceException serviceException = assertThrowsExactly(
                ServiceException.class, () -> postService.edit(1L, savedPost.getId() + 1, request)
        );
        assertEquals(POST_NOT_FOUND.getMessage(), serviceException.getMessage());
    }

    @Test
    @BlogserviceMockUser
    @DisplayName("글 수정 - 실패 - 작성자 아님")
    void update_post_fail_author_not_matched() {
        // given
        Post requestPost = Post.builder()
                .title("수정전제목").content("수정전내용")
                .user(securityContext.getCurrentUser())
                .build();
        Post savedPost = postRepository.save(requestPost);

        PostEdit.Request request = PostEdit.Request.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // expected
        ServiceException serviceException = assertThrowsExactly(
                ServiceException.class, () -> postService.edit
                        (securityContext.getCurrentUser().getId() + 1, savedPost.getId(), request)
        );
        assertEquals(POST_AUTHOR_NOT_MATCHING.getMessage(), serviceException.getMessage());
    }

    @Test
    @DisplayName("글 상세 조회 - 성공")
    void view_post_details_success() {
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

        // when
        PostResponse.Details response = postService.getDetails(savedPost.getId());

        // then
        assertNotNull(response);
        assertEquals("testtitle", response.getTitle());
        assertEquals("testcontent", response.getContent());
        assertEquals(savedPost.getCreatedAt(), response.getWriteDt());
        assertEquals(author.getId(), response.getAuthor().getId());
        assertEquals(author.getNickname(), response.getAuthor().getNickname());
    }

    @Test
    @DisplayName("글 상세 조회 - 실패 - 해당 글 없음")
    void view_post_details_fail_post_not_found() {
        // expected
        ServiceException serviceException =
                assertThrowsExactly(ServiceException.class, () -> postService.getDetails(999L));
        assertEquals(POST_NOT_FOUND.getMessage(), serviceException.getMessage());
    }

    @Test
    @DisplayName("글 상세 조회 - 실패 - 게시글 삭제됨")
    void view_post_details_fail_post_deleted() {
        // given
        User author = userRepository.save(User.builder().build());

        Post post = Post.builder()
                .title("testtitle")
                .content("testcontent")
                .user(author)
                .isDeleted(true)
                .build();
        Post savedPost = postRepository.save(post);

        // expected
        ServiceException serviceException =
                assertThrowsExactly(ServiceException.class, () -> postService.getDetails(savedPost.getId()));
        assertEquals(POST_DELETED.getMessage(), serviceException.getMessage());
    }

    @Test
    @DisplayName("글 조회 수 조회 - 성공")
    void view_post_view_counts_success() {

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

        // when
        PostResponse.VIEWS views = postService.getViewCounts(savedPost.getId());
        assertEquals(3L, views.getViews());
    }

    @Test
    @DisplayName("글 조회 수 조회 - 실패 - 해당 글 없음")
    void view_post_view_counts_fail_post_not_found() {
        // expected
        ServiceException serviceException =
                assertThrowsExactly(ServiceException.class, () -> postService.getViewCounts(999L));
        assertEquals(POST_NOT_FOUND.getMessage(), serviceException.getMessage());
    }

    @Test
    @DisplayName("글 조회 수 조회 - 실패 - 게시글 삭제됨")
    void view_post_view_counts_fail_post_deleted() {
        // given
        User author = userRepository.save(User.builder().build());

        Post post = Post.builder()
                .title("testtitle")
                .content("testcontent")
                .user(author)
                .isDeleted(true)
                .build();
        Post savedPost = postRepository.save(post);

        // expected
        ServiceException serviceException =
                assertThrowsExactly(ServiceException.class, () -> postService.getViewCounts(savedPost.getId()));
        assertEquals(POST_DELETED.getMessage(), serviceException.getMessage());
    }

    @Test
    @DisplayName("글 좋아요 수 조회 - 성공")
    void view_post_likes_counts_success() {
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

        // when
        PostResponse.LIKES likes = postService.getLikeCounts(savedPost.getId());
        assertEquals(3L, likes.getLikes());
    }

    @Test
    @DisplayName("글 좋아요 수 조회 - 실패 - 해당 글 없음")
    void view_post_like_counts_fail_post_not_found() {
        // expected
        ServiceException serviceException =
                assertThrowsExactly(ServiceException.class, () -> postService.getLikeCounts(999L));
        assertEquals(POST_NOT_FOUND.getMessage(), serviceException.getMessage());
    }

    @Test
    @DisplayName("글 좋아요 수 조회 - 실패 - 게시글 삭제됨")
    void view_post_like_counts_fail_post_deleted() {
        // given
        User author = userRepository.save(User.builder().build());

        Post post = Post.builder()
                .title("testtitle")
                .content("testcontent")
                .user(author)
                .isDeleted(true)
                .build();
        Post savedPost = postRepository.save(post);

        // expected
        ServiceException serviceException =
                assertThrowsExactly(ServiceException.class, () -> postService.getLikeCounts(savedPost.getId()));
        assertEquals(POST_DELETED.getMessage(), serviceException.getMessage());
    }

    @Test
    @DisplayName("글 첫 페이지 조회")
    void test3() {
        // given
         List<Post> requestPosts = IntStream.range(1, 20)
                         .mapToObj(i -> {
                             return Post.builder()
                                     .title("제목 " + i)
                                     .content("내용 " + i)
                                     .build();
                         })
                 .toList();
        postRepository.saveAll(requestPosts);

//        Pageable pageable = PageRequest.of(0, 5, Sort.by(DESC, "id"));
        PostSearch postSearch = PostSearch.builder()
                .page(1).size(10)
                .build();

        // when
        List<PostResponse> posts = postService.getList(postSearch);

        // then
        assertEquals(10L, posts.size());
        assertEquals("제목 19", posts.get(0).getTitle());
    }

//    @Test
//    @DisplayName("글 제목 수정")
//    void test4() {
//        // given
//        Post requestPost = Post.builder()
//                .title("수정전제목").content("수정전내용")
//                .build();
//        Post savedPost = postRepository.save(requestPost);
//
//        PostEdit.Request postEdit = PostEdit.Request.builder()
//                .title("수정후제목")
//                .build();
//
//        // when
//        postService.edit(savedPost.getId(), postEdit);
//
//        // then
//        Post changedPost = postRepository.findById(savedPost.getId())
//                .orElseThrow(() -> new RuntimeException
//                        ("글이 존재하지 않습니다. id: " + savedPost.getContent()));
//        assertEquals("수정후제목", changedPost.getTitle());
//        assertEquals("수정전내용", changedPost.getContent());
//    }

//    @Test
//    @DisplayName("글 내용 수정")
//    void test5() {
//        // given
//        Post requestPost = Post.builder()
//                .title("수정전제목").content("수정전내용")
//                .build();
//        Post savedPost = postRepository.save(requestPost);
//
//        PostEdit postEdit = PostEdit.Request.builder()
//                .content("수정후내용")
//                .build();
//
//        // when
//        postService.edit(savedPost.getId(), postEdit);
//
//        // then
//        Post changedPost = postRepository.findById(savedPost.getId())
//                .orElseThrow(() -> new RuntimeException
//                        ("글이 존재하지 않습니다. id: " + savedPost.getContent()));
//        assertEquals("수정전제목", changedPost.getTitle());
//        assertEquals("수정후내용", changedPost.getContent());
//    }

    @Test
    @DisplayName("게시글 삭제")
    void test6() {
        // given
        Post requestPost = Post.builder()
                .title("글제목").content("글내용")
                .build();
        Post savedPost = postRepository.save(requestPost);

        // when
        postService.delete(savedPost.getId());

        // then
        assertEquals(0, postRepository.count());
    }

    @Test
    @DisplayName("글 1개 조회 - 존재하지 않는 글")
    void test7() {
        // given
        Post post = Post.builder()
                .title("foo").content("bar")
                .build();
        postRepository.save(post);

        // expected
        assertThrows(PostNotFound.class, () -> postService.getDetails(post.getId() + 1));
    }

    @Test
    @DisplayName("게시글 삭제 - 존재하지 않는 글")
    void test8() {
        // given
        Post requestPost = Post.builder()
                .title("글제목").content("글내용")
                .build();
        Post savedPost = postRepository.save(requestPost);

        // expected
        assertThrows(PostNotFound.class, () -> postService.delete(savedPost.getId() + 1));
    }

//    @Test
//    @DisplayName("글 내용 수정 - 존재하지 않는 글")
//    void test9() {
//        // given
//        Post requestPost = Post.builder()
//                .title("수정전제목").content("수정전내용")
//                .build();
//        Post savedPost = postRepository.save(requestPost);
//
//        PostEdit postEdit = PostEdit.builder()
//                .content("수정후내용")
//                .build();
//
//        // expected
//        assertThrows(PostNotFound.class, () -> postService.edit(savedPost.getId() + 1, postEdit));
//    }

}
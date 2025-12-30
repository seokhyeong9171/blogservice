package com.blogservice.api.service;

import com.blogservice.api.domain.Post;
import com.blogservice.api.exception.PostNotFound;
import com.blogservice.api.repository.PostRepository;
import com.blogservice.api.request.PostCreate;
import com.blogservice.api.request.PostEdit;
import com.blogservice.api.request.PostSearch;
import com.blogservice.api.response.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clean() {
        jdbcTemplate.execute("TRUNCATE TABLE post");
        jdbcTemplate.execute("ALTER TABLE post ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    @DisplayName("글 작성")
    void test1() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // when
        postService.write(userPrincipal.getUserId(), postCreate);

        // then
        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().getFirst();
        assertEquals("제목입니다.", post.getTitle());
        assertEquals("내용입니다.", post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test2() {
        // given
        Post requestPost = Post.builder()
                .title("foo").content("bar")
                .build();
        postRepository.save(requestPost);

        // when
        PostResponse response = postService.get(requestPost.getId());

        // then
        assertNotNull(response);
        assertEquals(1L, postRepository.count());
        assertEquals("foo", response.getTitle());
        assertEquals("bar", response.getContent());
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

    @Test
    @DisplayName("글 제목 수정")
    void test4() {
        // given
        Post requestPost = Post.builder()
                .title("수정전제목").content("수정전내용")
                .build();
        Post savedPost = postRepository.save(requestPost);

        PostEdit postEdit = PostEdit.builder()
                .title("수정후제목")
                .build();

        // when
        postService.edit(savedPost.getId(), postEdit);

        // then
        Post changedPost = postRepository.findById(savedPost.getId())
                .orElseThrow(() -> new RuntimeException
                        ("글이 존재하지 않습니다. id: " + savedPost.getContent()));
        assertEquals("수정후제목", changedPost.getTitle());
        assertEquals("수정전내용", changedPost.getContent());
    }

    @Test
    @DisplayName("글 내용 수정")
    void test5() {
        // given
        Post requestPost = Post.builder()
                .title("수정전제목").content("수정전내용")
                .build();
        Post savedPost = postRepository.save(requestPost);

        PostEdit postEdit = PostEdit.builder()
                .content("수정후내용")
                .build();

        // when
        postService.edit(savedPost.getId(), postEdit);

        // then
        Post changedPost = postRepository.findById(savedPost.getId())
                .orElseThrow(() -> new RuntimeException
                        ("글이 존재하지 않습니다. id: " + savedPost.getContent()));
        assertEquals("수정전제목", changedPost.getTitle());
        assertEquals("수정후내용", changedPost.getContent());
    }

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
        assertThrows(PostNotFound.class, () -> postService.get(post.getId() + 1));
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

    @Test
    @DisplayName("글 내용 수정 - 존재하지 않는 글")
    void test9() {
        // given
        Post requestPost = Post.builder()
                .title("수정전제목").content("수정전내용")
                .build();
        Post savedPost = postRepository.save(requestPost);

        PostEdit postEdit = PostEdit.builder()
                .content("수정후내용")
                .build();

        // expected
        assertThrows(PostNotFound.class, () -> postService.edit(savedPost.getId() + 1, postEdit));
    }

}
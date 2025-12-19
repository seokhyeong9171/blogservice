package com.blogservice.api.service;

import com.blogservice.api.domain.Post;
import com.blogservice.api.repository.PostRepository;
import com.blogservice.api.request.PostCreate;
import com.blogservice.api.response.PostResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
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
        postService.write(postCreate);

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
         List<Post> requestPosts = IntStream.range(1, 31)
                         .mapToObj(i -> {
                             return Post.builder()
                                     .title("제목 " + i)
                                     .content("내용 " + i)
                                     .build();
                         })
                 .toList();
        postRepository.saveAll(requestPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(DESC, "id"));

        // when
        List<PostResponse> posts = postService.getList(pageable);

        // then
        assertEquals(5L, posts.size());
        assertEquals("제목 30", posts.get(0).getTitle());
        assertEquals("제목 26", posts.get(4).getTitle());
    }
}
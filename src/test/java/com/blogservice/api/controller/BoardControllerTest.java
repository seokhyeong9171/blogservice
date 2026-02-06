package com.blogservice.api.controller;

import com.blogservice.api.config.BlogserviceMockSecurityContext;
import com.blogservice.api.config.BlogserviceMockUser;
import com.blogservice.api.domain.post.Likes;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.Views;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.PostCreate;
import com.blogservice.api.dto.PostEdit;
import com.blogservice.api.repository.board.BoardRepository;
import com.blogservice.api.repository.post.LikeRepository;
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
class BoardControllerTest {

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

    @AfterEach
    void clean() {
        postRepository.deleteAll();
        userRepository.deleteAll();
        viewRepository.deleteAll();
        likeRepository.deleteAll();
    }

    @Test
    @DisplayName("게시판 별 게시글 수 조회")
    void getCountByBoard() throws Exception {
        // given
        User author = userRepository.save(User.builder().nickname("user").build());

        List<Post> requestPosts = new java.util.ArrayList<>(IntStream.range(1, 16)
                .mapToObj(i -> Post.builder()
                        .user(author)
                        .board(boardRepository.findById(1L).get())
                        .title("제목 " + i)
                        .content("내용 " + i)
                        .build())
                .toList());
        requestPosts.add(Post.builder()
                .user(author)
                .board(boardRepository.findById(2L).get())
                .title("제목")
                .content("내용")
                .build());

        postRepository.saveAll(requestPosts);

        // expected
        mockMvc.perform(get("/api/boards/{boardId}/count", 1L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postCount").value(15))
                .andDo(print());
    }

}
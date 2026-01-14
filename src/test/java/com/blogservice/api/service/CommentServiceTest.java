package com.blogservice.api.service;

import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.repository.comment.CommentRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.dto.request.comment.CommentCreate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void writeComment() {
        // given
        CommentCreate request = CommentCreate.builder()
                .author("author")
                .password("123456")
                .content("testcomment")
                .build();

        Post savedPost = postRepository.save(Post.builder().build());

        // when
        commentService.write(savedPost.getId(), request);

        // then
        assertEquals(1L, commentRepository.count());
        Comment findedComment = commentRepository.findAll().getFirst();
        assertEquals(findedComment.getUser(), request.getAuthor());
        assertEquals(findedComment.getContent(), request.getContent());
//        assertTrue(passwordEncoder.matches(request.getPassword(), findedComment.ge()));
    }
  
}
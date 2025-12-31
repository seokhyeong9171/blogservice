package com.blogservice.api.service;

import com.blogservice.api.domain.Comment;
import com.blogservice.api.domain.Post;
import com.blogservice.api.exception.PostNotFound;
import com.blogservice.api.repository.comment.CommentRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.request.comment.CommentCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final PasswordEncoder passwordEncoder;

    public void write(Long postId, CommentCreate request) {
        Post findedPost = postRepository.findById(postId).orElseThrow(PostNotFound::new);

        Comment comment = Comment.builder()
                .author(request.getAuthor())
                .password(passwordEncoder.encode(request.getPassword()))
                .content(request.getContent())
                .build();

        findedPost.addComment(comment);
    }
}

package com.blogservice.api.controller;

import com.blogservice.api.dto.request.comment.CommentCreate;
import com.blogservice.api.dto.request.comment.CommentDelete;
import com.blogservice.api.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public void write(@PathVariable Long postId, @RequestBody @Validated CommentCreate request) {
        commentService.write(postId, request);
    }
    
    @PostMapping("/comments/{commentId}/delete")
    public void delete(@PathVariable Long commentId, @RequestBody @Validated CommentDelete request) {
        commentService.delete(commentId, request);
    }
}

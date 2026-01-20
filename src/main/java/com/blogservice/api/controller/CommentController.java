package com.blogservice.api.controller;

import com.blogservice.api.config.UserPrincipal;
import com.blogservice.api.dto.CommentRequest;
import com.blogservice.api.dto.request.comment.CommentDelete;
import com.blogservice.api.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<Void> writeComment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId, @RequestBody @Validated CommentRequest.Create request) {
        commentService.write(userPrincipal.getUserId(), postId, request);
        return ResponseEntity.status(CREATED).build();
    }
    
    @PostMapping("/comments/{commentId}/delete")
    public void delete(@PathVariable Long commentId, @RequestBody @Validated CommentDelete request) {
        commentService.delete(commentId, request);
    }
}

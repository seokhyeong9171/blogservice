package com.blogservice.api.controller;

import com.blogservice.api.config.UserPrincipal;
import com.blogservice.api.dto.CommentDto;
import com.blogservice.api.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<Void> writeComment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId, @RequestBody @Validated CommentDto.Create request) {
        commentService.write(userPrincipal.getUserId(), postId, request);
        return ResponseEntity.status(CREATED).build();
    }

    @PatchMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId,
            @RequestBody @Validated CommentDto.Update request
    ) {

        commentService.update(userPrincipal.getUserId(), commentId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId) {
        commentService.delete(userPrincipal.getUserId(), commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentDto.Details> getComment(@PathVariable Long commentId) {
        CommentDto.Details response = commentService.getDetails(commentId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto.List>> getCommentsList(@PathVariable Long postId, Pageable pageable) {
        List<CommentDto.List> response = commentService.getCommentsList(postId, pageable);
        return ResponseEntity.ok(response);
    }

}

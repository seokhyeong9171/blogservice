package com.blogservice.api.controller;

import com.blogservice.api.config.UserPrincipal;
import com.blogservice.api.dto.PostCreate;
import com.blogservice.api.dto.PostEdit;
import com.blogservice.api.dto.PostResponse;
import com.blogservice.api.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/api/boards/{boardId}/posts")
    public ResponseEntity<PostCreate.Response> writePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long boardId,
            @RequestBody @Validated PostCreate.Request request
            ) {
        PostCreate.Response response = postService.write(userPrincipal.getUserId(), boardId, request);
        // todo
        //  snapshot 생성
        return ResponseEntity.status(CREATED).body(response);
    }

    @PatchMapping("/api/posts/{postId}")
    public ResponseEntity<PostEdit.Response> editPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId, @RequestBody @Validated PostEdit.Request request
    ) {
        PostEdit.Response response = postService.edit(userPrincipal.getUserId(), postId, request);
        // todo
        //  snapshot 생성
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/posts/{postId}")
    public ResponseEntity<Void> deletePost(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId) {
        postService.delete(userPrincipal.getUserId(), postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/posts/{postId}")
    public ResponseEntity<PostResponse.Details> getDetails(@PathVariable Long postId) {
        PostResponse.Details details = postService.getDetails(postId);
        // todo
        //  view 객체 생성 로직
        return ResponseEntity.ok(details);
    }

    @GetMapping("/api/boards/{boardId}/posts")
    public ResponseEntity<List<PostResponse.List>> getList(
            @PathVariable Long boardId, @RequestParam int page, @RequestParam int size
    ) {
        List<PostResponse.List> response = postService.getList(boardId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/posts/{postId}/views")
    public ResponseEntity<PostResponse.Views> getViewCounts(@PathVariable Long postId) {
        PostResponse.Views response = postService.getViewCounts(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/posts/{postId}/likes")
    public ResponseEntity<PostResponse.Likes> getLikeCounts(@PathVariable Long postId) {
//        log.info("postId: {}", postId);
        PostResponse.Likes response = postService.getLikeCounts(postId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/posts/{postId}/likes")
    public ResponseEntity<PostResponse.Likes> likePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId) {
        PostResponse.Likes response = postService.likePost(userPrincipal.getUserId(), postId);
        return ResponseEntity.ok(response);
    }



}

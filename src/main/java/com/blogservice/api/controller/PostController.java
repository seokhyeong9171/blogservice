package com.blogservice.api.controller;

import com.blogservice.api.config.UserPrincipal;
import com.blogservice.api.dto.PostCreate;
import com.blogservice.api.dto.PostEdit;
import com.blogservice.api.dto.request.post.PostSearch;
import com.blogservice.api.dto.PostResponse;
import com.blogservice.api.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostCreate.Response> writePost
            (@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Validated PostCreate.Request request) {
        PostCreate.Response response = postService.write(userPrincipal.getUserId(), request);
        // todo
        //  snapshot 생성
        return ResponseEntity.status(CREATED).body(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostEdit.Response> editPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId, @RequestBody @Validated PostEdit.Request request
    ) {
        PostEdit.Response response = postService.edit(userPrincipal.getUserId(), postId, request);
        // todo
        //  snapshot 생성
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId) {
        postService.delete(userPrincipal.getUserId(), postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse.Details> getDetails(@PathVariable Long postId) {
        PostResponse.Details details = postService.getDetails(postId);
        // todo
        //  view 객체 생성 로직
        return ResponseEntity.ok(details);
    }

    @GetMapping("/{postId}/views")
    public ResponseEntity<PostResponse.VIEWS> getViewCounts(@PathVariable Long postId) {
        PostResponse.VIEWS response = postService.getViewCounts(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<PostResponse.LIKES> getLikeCounts(@PathVariable Long postId) {
        PostResponse.LIKES response = postService.getLikeCounts(postId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<PostResponse.LIKES> likePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId) {
        PostResponse.LIKES response = postService.likePost(userPrincipal.getUserId(), postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts")
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch) {
        return postService.getList(postSearch);
    }



}

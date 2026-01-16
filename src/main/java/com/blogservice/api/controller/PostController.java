package com.blogservice.api.controller;

import com.blogservice.api.config.UserPrincipal;
import com.blogservice.api.dto.PostCreate;
import com.blogservice.api.dto.request.post.PostEdit;
import com.blogservice.api.dto.request.post.PostSearch;
import com.blogservice.api.dto.response.PostResponse;
import com.blogservice.api.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping()
    public ResponseEntity<PostCreate.Response> post
            (@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Validated PostCreate.Request request) {
        PostCreate.Response response = postService.write(userPrincipal.getUserId(), request);
        return ResponseEntity.status(CREATED).body(response);
    }

    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        return postService.get(postId);
    }

    @GetMapping("/posts")
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch) {
        return postService.getList(postSearch);
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN') && hasPermission(#postId, 'POST', 'PATCH')")
    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable Long postId, @RequestBody @Validated PostEdit request) {
        postService.edit(postId, request);
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @PreAuthorize("hasRole('ROLE_ADMIN') && hasPermission(#postId, 'POST', 'DELETE')")
    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }


}

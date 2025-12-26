package com.blogservice.api.controller;

import com.blogservice.api.config.data.UserSession;
import com.blogservice.api.request.PostCreate;
import com.blogservice.api.request.PostEdit;
import com.blogservice.api.request.PostSearch;
import com.blogservice.api.response.PostResponse;
import com.blogservice.api.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/test")
    public Long test(UserSession userSession) {
        log.info(">>>{}", userSession.id);
        return userSession.id;
    }

    @GetMapping("/test2")
    public String test2() {
        return "인증이 필요 없는 페이지";
    }

    @PostMapping("/posts")
    public Map<String, String> post(@RequestBody @Validated PostCreate request) {
        postService.write(request);
        return Map.of();
    }

    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        return postService.get(postId);
    }

    @GetMapping("/posts")
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch) {
        return postService.getList(postSearch);
    }

    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable Long postId, @RequestBody @Validated PostEdit request) {
        postService.edit(postId, request);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }


}

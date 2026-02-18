package com.blogservice.api.controller;

import com.blogservice.api.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/permit")
    public String permitall(){
        return "permitall";
    }

    @GetMapping("/auth")
    public String authenticated(){
        return "authenticated";
    }

    @GetMapping("/post-like-count-init")
    public void postLikeCountInit(){
        for (long i = 1L; i <= 12000000L; i++) {
            testService.initPostLikeCount(i);
        }
    }

}

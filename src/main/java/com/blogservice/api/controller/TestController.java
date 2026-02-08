package com.blogservice.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/permit")
    public String permitall(){
        return "permitall";
    }

    @GetMapping("/auth")
    public String authenticated(){
        return "authenticated";
    }

}

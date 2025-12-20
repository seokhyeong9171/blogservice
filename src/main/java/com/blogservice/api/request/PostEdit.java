package com.blogservice.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostEdit {

    @NotBlank(message = "타이틀을 입력하세요.")
    public String title;

    @NotBlank(message = "컨텐츠을 입력해주세요.")
    public String content;

    @Builder
    public PostEdit(String title, String content) {
        this.title = title;
        this.content = content;
    }
}

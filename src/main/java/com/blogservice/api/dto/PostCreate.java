package com.blogservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import static lombok.AccessLevel.*;

@ToString
@Setter
@Getter
public class PostCreate {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Request {
        @NotBlank(message = "타이틀을 입력하세요.")
        public String title;

        @NotBlank(message = "컨텐츠을 입력해주세요.")
        public String content;

    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Response {
        private String postId;
    }
}

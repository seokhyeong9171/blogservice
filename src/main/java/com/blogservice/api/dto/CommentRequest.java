package com.blogservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import static lombok.AccessLevel.*;

public class CommentRequest {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Create {

        @Length(min = 10, max = 1000, message = "내용은 10~1000자까지 입력해주세요.")
        @NotBlank(message = "내용을 입력해주세요.")
        private String content;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Update {

        @Length(min = 10, max = 1000, message = "내용은 10~1000자까지 입력해주세요.")
        @NotBlank(message = "내용을 입력해주세요.")
        private String content;
    }

}

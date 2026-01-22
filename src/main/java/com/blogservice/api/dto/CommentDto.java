package com.blogservice.api.dto;

import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static lombok.AccessLevel.*;

public class CommentDto {

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

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class List {
        private Long commentId;
        private Boolean isDeleted;
        private Boolean existChild;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Details {

        private String content;
        private LocalDateTime writeDt;

        private Author author;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor(access = PROTECTED)
        @Builder
        public static class Author {
            private Long id;
            private String nickname;

            public static Author fromUser(User user) {
                return Author.builder()
                        .id(user.getId())
                        .nickname(user.getNickname())
                        .build();
            }
        }

        public static Details fromEntity(Comment comment) {
            return Details.builder()
                    .content(comment.getContent())
                    .writeDt(comment.getCreatedAt())
                    .author(Author.fromUser(comment.getUser()))
                    .build();
        }
    }

}

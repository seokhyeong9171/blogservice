package com.blogservice.api.dto;

import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Getter
public class PostResponse {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Details {
        private String title;
        private String content;
        private LocalDateTime writeDt;

        private Author author;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class List {
        private Long postId;
        private String title;
        private Long views;
        private Long likes;

        private Author author;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Author {
        private Long id;
        private String nickname;

        public static Author of(Post post) {
            User author = post.getUser();
            return Author.builder()
                    .id(author.getId())
                    .nickname(author.getNickname())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Views {
        private Long views;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Likes {
        private Long likes;
    }

}

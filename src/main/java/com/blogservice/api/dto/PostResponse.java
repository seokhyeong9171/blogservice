package com.blogservice.api.dto;

import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.user.User;
import lombok.*;

import java.time.LocalDateTime;

import static lombok.AccessLevel.*;

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
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class VIEWS {
        private Long views;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class LIKES {
        private Long likes;
    }



    private final Long id;
    private final String title;
    private final String content;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
    }

    @Builder
    public PostResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title.substring(0, Math.min(title.length(), 10));
        this.content = content;
    }
}

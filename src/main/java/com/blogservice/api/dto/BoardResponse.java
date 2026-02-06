package com.blogservice.api.dto;

import com.blogservice.api.domain.board.Board;
import com.blogservice.api.domain.board.BoardName;
import lombok.*;

import static lombok.AccessLevel.*;

public class BoardResponse {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class List {

        private Long boardId;
        private BoardName name;

        public static List from(Board board) {
            return List.builder()
                    .boardId(board.getId())
                    .name(board.getName())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Builder
    public static class Count {
        private Long postCount;

        public static Count from(Long postCount) {
            return new Count(postCount);
        }
    }
}

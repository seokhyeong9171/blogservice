package com.blogservice.api.dto;

import com.blogservice.api.domain.board.Board;
import com.blogservice.api.domain.board.BoardName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

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

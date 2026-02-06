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
    public static class ListResponse {

        private Long boardId;
        private BoardName name;

        public static ListResponse from(Board board) {
            return ListResponse.builder()
                    .boardId(board.getId())
                    .name(board.getName())
                    .build();
        }
    }

}

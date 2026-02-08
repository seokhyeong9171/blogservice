package com.blogservice.api.domain.board;

import lombok.Getter;

@Getter
public enum BoardName {

    FREE("자유게시판"),
    QNA("질의응답"),
    CS("고객센터");

    private final String name;

    BoardName(String name) {
        this.name = name;
    }
}

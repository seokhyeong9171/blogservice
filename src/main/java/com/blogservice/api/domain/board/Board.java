package com.blogservice.api.domain.board;

import com.blogservice.api.domain.BaseTimeEntity;
import com.blogservice.api.domain.post.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(STRING)
    private BoardName name;

    @OneToMany(mappedBy = "board", cascade = ALL)
    private List<Post> posts = new ArrayList<>();

//    @Builder
//    public Board(BoardName name, List<Post> posts) {
//        this.name = name;
//        this.posts = posts;
//    }
@Builder
public Board(BoardName name) {
    this.name = name;
}
}

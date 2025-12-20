package com.blogservice.api.domain;

import com.blogservice.api.request.PostEdit;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PUBLIC;

@Entity
@Getter
@NoArgsConstructor(access = PUBLIC)
public class Post {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;

    @Builder
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void change(PostEdit postEdit) {
        this.title = postEdit.getTitle();
        this.content = postEdit.getContent();
    }
}

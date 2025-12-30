package com.blogservice.api.domain;

import com.blogservice.api.request.PostEdit;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Post(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public void edit(PostEdit postEdit) {
        if (postEdit.getTitle() != null) {
            title = postEdit.getTitle();
        }
        if (postEdit.getContent() != null) {
            content = postEdit.getContent();
        }
    }

    public Long getUserId() {
        return user.getId();
    }
}

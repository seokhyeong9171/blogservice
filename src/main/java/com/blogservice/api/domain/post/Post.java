package com.blogservice.api.domain.post;

import com.blogservice.api.domain.BaseTimeEntity;
import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.PostEdit;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PUBLIC;

@Entity
@Getter
@NoArgsConstructor(access = PUBLIC)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean isDeleted;

    @OneToMany(mappedBy = "post", cascade = ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = ALL)
    private List<Views> views = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = ALL)
    private List<Likes> likes = new ArrayList<>();

    @Builder
    public Post(String title, String content, User user, boolean isDeleted) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.isDeleted = isDeleted;
    }

    public void edit(PostEdit.Request request) {
        if (request.getTitle() != null) {
            this.title = request.getTitle();
        }
        if (request.getContent() != null) {
            this.content = request.getContent();
        }
    }

    public void delete() {
        this.isDeleted = true;
    }

    public Long getUserId() {
        return user.getId();
    }

}

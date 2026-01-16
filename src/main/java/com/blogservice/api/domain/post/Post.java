package com.blogservice.api.domain.post;

import com.blogservice.api.domain.BaseTimeEntity;
import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.request.post.PostEdit;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<View> views = new ArrayList<>();

    @Builder
    public Post(String title, String content, User user, boolean isDeleted) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.isDeleted = isDeleted;
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

    public void addComment(Comment comment) {
        comment.setPost(this);
        comments.add(comment);
    }
}

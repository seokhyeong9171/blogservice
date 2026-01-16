package com.blogservice.api.domain.comment;

import com.blogservice.api.domain.BaseTimeEntity;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Table(
        indexes =
                @Index(name = "IDX_COMMENT_POST_ID", columnList = "post_id")
)
@NoArgsConstructor(access = PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> childComments = new ArrayList<>();

//    @Column(nullable = false)
//    private String author;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String content;

    @Setter
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private boolean isDeleted;

    @Builder
    public Comment(
            Comment parentComment, User user, String content, Post post, boolean isDeleted
    ) {
        this.parentComment = parentComment;
        this.user = user;
        this.content = content;
        this.post = post;
        this.isDeleted = isDeleted;
    }
}


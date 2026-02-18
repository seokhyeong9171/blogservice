package com.blogservice.api.domain.comment;


import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.PostLikeCount;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class PostCommentCount {

    @Id
    private Long postId;

    @MapsId
    @OneToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private Long count;

    @Builder
    public PostCommentCount(Post post, Long count) {
        this.post = post;
        this.count = count;
    }

    public static PostCommentCount create(Post post) {
        return PostCommentCount.builder()
                .post(post)
                .count(0L)
                .build();
    }

}

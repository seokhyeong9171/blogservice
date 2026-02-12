package com.blogservice.api.domain.post;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class PostViewCount {

    @Id
    private Long postId;

    @MapsId
    @OneToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private Long count;

    @Builder
    public PostViewCount(Post post, Long count) {
        this.post = post;
        this.count = count;
    }

    public static PostViewCount create(Post post) {
        return PostViewCount.builder()
                .post(post)
                .count(0L)
                .build();
    }

    public void update(Long count) {
        if (count < this.count) {
            return;
        }

        this.count = count;
    }
}

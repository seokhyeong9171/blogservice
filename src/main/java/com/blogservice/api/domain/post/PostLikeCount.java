package com.blogservice.api.domain.post;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLikeCount {

    @Id
    private Long postId;

    @MapsId
    @OneToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private Long count;

    @Builder
    public PostLikeCount(Post post, Long count) {
        this.post = post;
        this.count = count;
    }

    public static PostLikeCount create(Post post) {
        return PostLikeCount.builder()
                .post(post)
                .count(0L)
                .build();
    }

    public void update(Long count) {
        this.count = count;
    }

    public void increment() {
        this.count++;
    }

    public void decrement() {
        this.count--;
    }

}

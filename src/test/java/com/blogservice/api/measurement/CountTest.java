package com.blogservice.api.measurement;

import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.PostLikeCount;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.repository.post.LikeRepository;
import com.blogservice.api.repository.post.PostLikeCountRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.service.PostService;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mysema.commons.lang.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("measurement")
public class CountTest {

    @Autowired
    private PostService postService;

    @Autowired
    private EntityManager em;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private PostLikeCountRepository postLikeCountRepository;



    //    @Test
    void 동시에_100명이_좋아요를_누른다() throws InterruptedException {
        int threadCount = 100;
        // 모든 스레드가 준비될 때까지 기다렸다가 동시에 시작하게 해주는 장치
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Post post = em.getReference(Post.class, 1000L);

        for (int i = 0; i < threadCount; i++) {
            int j = i;
            executorService.submit(() -> {
                try {
                    // 좋아요 증가 로직 호출
                    postService.likePost((long) j, 1000L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기

        // 결과 확인
        // 100명이 눌렀으니 like_count는 정확히 100이어야 함
        assertEquals(100L, likeRepository.countByPost(postRepository.findById(1000L).get()));
    }

    @Test
    @Transactional
    void init_post_like_count() throws InterruptedException {
            for (long i = 1L; i <= 12000000L; i++) {
                try {
                    Post post = postRepository.findById(i).get();
                    long count = likeRepository.countByPost(post);
//                    PostLikeCount postLikeCount = PostLikeCount.create(i, count);
//                    postLikeCountRepository.save(postLikeCount);
//                    em.persist(postLikeCount);
                } catch (Exception e) {
                    log.error("postId={}", i, e);
                }

                if (i % 1000 == 0) { // 20개마다 한 번씩 DB에 반영하고 메모리 비우기
                    log.info("postId={}", i);
                    em.flush();
                    em.clear();
                }

            }

    }
}

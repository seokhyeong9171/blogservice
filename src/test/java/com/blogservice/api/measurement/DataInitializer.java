package com.blogservice.api.measurement;

import com.blogservice.api.domain.board.Board;
import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.post.Likes;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.Views;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.repository.board.BoardRepository;
import com.blogservice.api.repository.comment.CommentRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.repository.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static com.blogservice.api.domain.user.Role.ROLE_USER;

@ActiveProfiles("measurement")
@SpringBootTest
public class DataInitializer {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TransactionTemplate tx;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);

    static final int BULK_INSERT_SIZE = 2000;
    static final int EXECUTE_COUNT = 6000;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CommentRepository commentRepository;

    //    @Test
    void initializeUsers() throws Exception{
        Faker faker = new Faker();

        List<User> requestUsers = IntStream.range(1, 31)
                .mapToObj(i -> User.builder()
                        .nickname(faker.funnyName().name())
                        .name(faker.name().fullName())
                        .email(faker.internet().emailAddress())
                        .phone(faker.phoneNumber().cellPhone())
                        .birthDt(faker.timeAndDate().birthday())
                        .role(ROLE_USER)
                        .build())
                .toList();
        userRepository.saveAll(requestUsers);
    }


//    @Test
    void initializePost() throws InterruptedException {
        List<Long> userIds = userRepository.findAll().stream().map(User::getId).toList();
        List<Long> boardIds = boardRepository.findAll().stream().map(Board::getId).toList();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Random random = new Random();

        for (int i = 0; i < EXECUTE_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    tx.executeWithoutResult(status -> {
                        Faker faker = new Faker();
                        for (int j = 0; j < BULK_INSERT_SIZE; j++) {
                            User user = em.getReference(User.class, userIds.get(random.nextInt(userIds.size())));
                            Board board = em.getReference(Board.class, boardIds.get(random.nextInt(boardIds.size())));

                            Post post = Post.builder()
                                    .title(faker.book().title())
                                    .content(faker.lorem().sentence(5))
                                    .user(user)
                                    .board(board)
                                    .build();
                            em.persist(post);
                        }
                        em.flush();
                        em.clear();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
    }

//    @Test
    void initializeComment() throws InterruptedException {
        List<Long> userIds = userRepository.findAll().stream().map(User::getId).toList();
//        List<Long> boardIds = boardRepository.findAll().stream().map(Board::getId).toList();
//        List<Long> postIds = postRepository.findAll().stream().map(Post::getId).toList();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Random random = new Random();

        for (int i = 0; i < EXECUTE_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    tx.executeWithoutResult(status -> {
                        Faker faker = new Faker();
                        for (int j = 0; j < BULK_INSERT_SIZE; j++) {
                            User user = em.getReference(User.class, userIds.get(random.nextInt(userIds.size())));
//                            Board board = em.getReference(Board.class, boardIds.get(random.nextInt(boardIds.size())));
                            Post post = em.getReference(Post.class, random.nextInt(10) + 1);

                            Comment comment = Comment.builder()
                                    .post(post)
                                    .user(user)
                                    .content(faker.lorem().sentence(3))
                                    .build();
                            em.persist(comment);
                        }
                        em.flush();
                        em.clear();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
    }

//    @Test
    void initializeLike() throws InterruptedException {
        List<Long> userIds = userRepository.findAll().stream().map(User::getId).toList();
//        List<Long> boardIds = boardRepository.findAll().stream().map(Board::getId).toList();
//        List<Long> postIds = postRepository.findAll().stream().map(Post::getId).toList();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Random random = new Random();

        for (int i = 0; i < EXECUTE_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    tx.executeWithoutResult(status -> {
//                        Faker faker = new Faker();
                        for (int j = 0; j < BULK_INSERT_SIZE; j++) {
                            User user = em.getReference(User.class, userIds.get(random.nextInt(userIds.size())));
//                            Board board = em.getReference(Board.class, boardIds.get(random.nextInt(boardIds.size())));
                            Post post = em.getReference(Post.class, random.nextInt(12000000) + 1);

                            Likes likes = Likes.builder()
                                    .post(post)
                                    .user(user)
                                    .build();
                            em.persist(likes);
                        }
                        em.flush();
                        em.clear();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
    }

//    @Test
    void initializeView() throws InterruptedException {
        List<Long> userIds = userRepository.findAll().stream().map(User::getId).toList();
//        List<Long> boardIds = boardRepository.findAll().stream().map(Board::getId).toList();
//        List<Long> postIds = postRepository.findAll().stream().map(Post::getId).toList();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Random random = new Random();

        for (int i = 0; i < EXECUTE_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    tx.executeWithoutResult(status -> {
//                        Faker faker = new Faker();
                        for (int j = 0; j < BULK_INSERT_SIZE; j++) {
                            User user = em.getReference(User.class, userIds.get(random.nextInt(userIds.size())));
//                            Board board = em.getReference(Board.class, boardIds.get(random.nextInt(boardIds.size())));
                            Post post = em.getReference(Post.class, random.nextInt(100) + 1);

                            Views views = Views.builder()
                                    .post(post)
                                    .user(user)
                                    .build();
                            em.persist(views);
                        }
                        em.flush();
                        em.clear();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
    }

    @Test
    void initialRedisViewData() throws Exception {
        Random random = new Random();
        String key = "post-view-count:post:%s";

        for (int i = 1; i <= 100; i++) {

            redisTemplate.opsForValue().set(String.format(key, i), String.valueOf(random.nextInt(1000) % 1000));
        }
    }
}

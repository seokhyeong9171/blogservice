package com.blogservice.api.service;

import com.blogservice.api.domain.board.Board;
import com.blogservice.api.domain.post.Likes;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.post.PostLikeCount;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.PostCreate;
import com.blogservice.api.dto.PostEdit;
import com.blogservice.api.dto.PostResponse;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.board.BoardRepository;
import com.blogservice.api.repository.post.LikeRepository;
import com.blogservice.api.repository.post.PostLikeCountRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.repository.post.ViewRepository;
import com.blogservice.api.repository.user.UserRepository;
import com.blogservice.api.util.PageQueryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.blogservice.api.exception.ErrorCode.*;
import static com.blogservice.api.util.PageQueryUtil.*;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final ViewRepository viewRepository;
    private final BoardRepository boardRepository;
    private final PostLikeCountRepository postLikeCountRepository;

    public PostCreate.Response write(Long userId, Long boardId, PostCreate.Request request) {
        User user = findUserById(userId);
        Board board = findBoardById(boardId);

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .board(board)
                .isDeleted(false)
                .build();

        Post savedPost = postRepository.save(post);

        // todo
        //  snapshot


        return PostCreate.Response.builder()
                .postId(savedPost.getId())
                .build();
    }

    public PostEdit.Response edit(Long userId, Long postId, PostEdit.Request request) {
        Post findPost = findPostById(postId);

        verifyAuthor(userId, findPost);

        findPost.edit(request);

        // todo
        //  snapshot

        return PostEdit.Response.builder()
                .postId(postId)
                .build();
    }

    @Transactional(readOnly = true)
    public PostResponse.Details getDetails(Long postId) {
        Post post = findPostById(postId);

        verifyPostDeleted(post);

        return PostResponse.Details.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .writeDt(post.getCreatedAt())
                .author(PostResponse.Author.of(post))
                .build();
    }

    @Transactional(readOnly = true)
    public List<PostResponse.List> getList(Long boardId, int page, int size) {
        page = getOffset(page, size);
        size = getSize(size);
        List<Post> postList = postRepository.findPostList(boardId, page, size);
//        List<Post> postList = postRepository.getList(boardId, page, size);

        return postList.stream().map(post -> {
            long views = viewRepository.countByPost(post);
            long likes = likeRepository.countByPost(post);
            return PostResponse.List.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .views(views)
                    .likes(likes)
                    .author(PostResponse.Author.of(post))
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponse.Views getViewCounts(Long postId) {
        Post findPost = findPostById(postId);

        verifyPostDeleted(findPost);

        Long viewCount = viewRepository.countByPost(findPost);

        return PostResponse.Views.builder()
                .views(viewCount)
                .build();
    }

    @Transactional(readOnly = true)
    public PostResponse.Likes getLikeCounts(Long postId) {
        Post findPost = findPostById(postId);

        verifyPostDeleted(findPost);

//        Long likeCount = likeRepository.countByPost(findPost);
        PostLikeCount postLikeCount = postLikeCountRepository.findByPost(findPost)
                .orElseGet(() -> postLikeCountRepository.save(PostLikeCount.create(findPost)));

        return PostResponse.Likes.builder()
                .likes(postLikeCount.getCount())
                .build();
    }

    public PostResponse.Likes likePost(Long userId, Long postId) {
        User findUser = findUserById(userId);
        Post findPost = findPostById(postId);

        verifyPostDeleted(findPost);

        Optional<Likes> likes = likeRepository.findByUserIdAndPostId(userId, postId);
        if (likes.isPresent()) {
            likeRepository.delete(likes.get());
            postLikeCountRepository.decrementCount(postId);
        } else {
            Likes like = com.blogservice.api.domain.post.Likes.builder()
                    .user(findUser).post(findPost)
                    .build();
            likeRepository.save(like);
            postLikeCountRepository.incrementCount(postId);
        }


        return PostResponse.Likes.builder()
                .likes(
                        postLikeCountRepository.findByPost(findPost)
                                .orElseThrow(() -> new ServiceException(POST_LIKE_COUNT_NOT_FOUND))
                                .getCount()
                )
                .build();
    }

    public void delete(Long userId, Long postId) {
        Post findPost = findPostById(postId);

        verifyPostDeleted(findPost);

        verifyAuthor(userId, findPost);

        // todo
        //  snapshot

        findPost.delete();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ServiceException(USER_NOT_FOUND));
    }

    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new ServiceException(BOARD_NOT_FOUND));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ServiceException(POST_NOT_FOUND));
    }

    private void verifyAuthor(Long userId, Post findPost) {
        if (!Objects.equals(findPost.getUser().getId(), userId)) {
            throw new ServiceException(POST_AUTHOR_NOT_MATCHING);
        }
    }

    private static void verifyPostDeleted(Post post) {
        if (post.isDeleted()) {
            throw new ServiceException(POST_DELETED);
        }
    }
}

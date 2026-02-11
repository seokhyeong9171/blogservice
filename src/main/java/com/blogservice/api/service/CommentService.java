package com.blogservice.api.service;

import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.comment.PostCommentCount;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.CommentDto;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.comment.CommentRepository;
import com.blogservice.api.repository.comment.PostCommentCountRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.blogservice.api.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostCommentCountRepository postCommentCountRepository;

    public void write(Long userId, Long postId, CommentDto.Create request) {
        Post findPost = findPostById(postId);
        User findUser = findUserById(userId);

        verifyPostDeleted(findPost);

        Comment comment = com.blogservice.api.domain.comment.Comment.builder()
                .user(findUser)
                .post(findPost)
                .content(request.getContent())
                .isDeleted(false)
                .build();
        commentRepository.save(comment);

        postCommentCountRepository.incrementCount(postId);

        // todo
        //  comment snapshot
    }

    public void writeChild(Long userId, Long commentId, CommentDto.Create request) {
        Comment findComment = findCommentById(commentId);
        User findUser = findUserById(userId);

        verifyCommentDeleted(findComment);

        Comment comment = com.blogservice.api.domain.comment.Comment.builder()
                .user(findUser)
                .parentComment(findComment)
                .content(request.getContent())
                .isDeleted(false)
                .build();
        commentRepository.save(comment);

        postCommentCountRepository.incrementCount(commentId);

        // todo
        //  comment snapshot
    }

    public void update(Long userId, Long commentId, CommentDto.Update request) {
        Comment findComment = findCommentById(commentId);

        verifyCommentAuthor(userId, findComment);
        verifyPostDeleted(findComment.getPost() );
        verifyCommentDeleted(findComment);

        findComment.update(request.getContent());
        // todo
        //  comment snapshot
    }

    public void delete(Long userId, Long commentId) {
        Comment findComment = findCommentById(commentId);

        verifyCommentAuthor(userId, findComment);
        verifyCommentDeleted(findComment);

        findComment.delete();

        postCommentCountRepository.decrementCount(commentId);
        // todo
        //  comment snapshot
    }

    @Transactional(readOnly = true)
    public List<CommentDto.List> getCommentsList(Long postId, Pageable pageable) {

        Post findPost = findPostById(postId);
        verifyPostDeleted(findPost);

        return commentRepository.findAllParentComments(postId, pageable.getPageSize(), pageable.getPageNumber());
    }

    @Transactional(readOnly = true)
    public List<CommentDto.List> getChildCommentsList(Long commentId, Pageable pageable) {
        verifyCommentDeleted(findCommentById(commentId));

        return commentRepository.findAllChildComments(commentId, pageable.getPageSize(), pageable.getPageNumber());
    }

    @Transactional(readOnly = true)
    public CommentDto.Details getDetails(Long commentId) {
        Comment findComment = findCommentById(commentId);
        verifyCommentDeleted(findComment);
        return CommentDto.Details.fromEntity(findComment);
    }

    @Transactional(readOnly = true)
    public CommentDto.Count getCommentCount(Long postId) {
        Post findPost = findPostById(postId);
        verifyPostDeleted(findPost);

        PostCommentCount postCommentCount = postCommentCountRepository.findByPost(findPost)
                .orElseGet(() -> postCommentCountRepository.save(PostCommentCount.create(findPost)));

        return CommentDto.Count.from(postCommentCount.getCount());
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ServiceException(USER_NOT_FOUND));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new ServiceException(POST_NOT_FOUND));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new ServiceException(COMMENT_NOT_FOUND));
    }

    private void verifyPostDeleted(Post post) {
        if (post.isDeleted()) {
            throw new ServiceException(POST_DELETED);
        }
    }

    private void verifyCommentDeleted(Comment comment) {
        if (comment.isDeleted()) {
            throw new ServiceException(COMMENT_DELETED);
        }
    }

    private void verifyCommentAuthor(Long userId, com.blogservice.api.domain.comment.Comment comment) {
        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new ServiceException(COMMENT_AUTHOR_NOT_MATCHING);
        }
    }
}

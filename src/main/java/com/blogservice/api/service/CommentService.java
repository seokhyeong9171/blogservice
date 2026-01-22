package com.blogservice.api.service;

import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.CommentDto;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.comment.CommentRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.blogservice.api.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

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
        // todo
        //  comment snapshot
    }

    @Transactional(readOnly = true)
    public List<CommentDto.List> getCommentsList(Long postId, Pageable pageable) {

        Post findPost = findPostById(postId);
        verifyPostDeleted(findPost);

        List<CommentDto.List> comments = commentRepository.findAllParentComments(postId, pageable.getPageSize(), pageable.getPageNumber());
//        List<CommentDto.List> response = comments.stream().map(comment -> {
//            Comment p = comment.getParentComment();
//            boolean existChild;
//            existChild = p == null;
//            return CommentDto.List.builder()
//                    .commentId(comment.getId())
//                    .existChild(existChild)
//                    .isDeleted(comment.isDeleted())
//                    .build();
//        }).toList();
        return comments;
    }

    @Transactional(readOnly = true)
    public CommentDto.Details getDetails(Long commentId) {
        Comment findComment = findCommentById(commentId);
        verifyCommentDeleted(findComment);
        return CommentDto.Details.fromEntity(findComment);
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

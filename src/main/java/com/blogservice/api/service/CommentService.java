package com.blogservice.api.service;

import com.blogservice.api.domain.comment.Comment;
import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.CommentRequest;
import com.blogservice.api.dto.request.comment.CommentDelete;
import com.blogservice.api.exception.CommentNotFound;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.comment.CommentRepository;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.blogservice.api.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public void write(Long userId, Long postId, CommentRequest.Create request) {
        Post findPost = findPostById(postId);
        User findUser = findUserById(userId);

        verifyPostDeleted(findPost);

        Comment comment = Comment.builder()
                .user(findUser)
                .post(findPost)
                .content(request.getContent())
                .isDeleted(false)
                .build();
        commentRepository.save(comment);

        // todo
        //  comment snapshot
    }

    public void delete(Long commentId, CommentDelete request) {
        Comment findedComment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFound::new);

//        boolean isMatch = passwordEncoder.matches(request.getPassword(), findedComment.getPassword());
//        if(!isMatch) {
//            throw new InvalidPassword();
//        }

        commentRepository.delete(findedComment);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ServiceException(USER_NOT_FOUND));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new ServiceException(POST_NOT_FOUND));
    }

    private static void verifyPostDeleted(Post post) {
        if (post.isDeleted()) {
            throw new ServiceException(POST_DELETED);
        }
    }
}

package com.blogservice.api.service;

import com.blogservice.api.domain.post.Post;
import com.blogservice.api.domain.user.User;
import com.blogservice.api.dto.PostCreate;
import com.blogservice.api.dto.request.post.PostEdit;
import com.blogservice.api.dto.request.post.PostSearch;
import com.blogservice.api.dto.response.PostResponse;
import com.blogservice.api.exception.ErrorCode;
import com.blogservice.api.exception.PostNotFound;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.exception.UserNotFound;
import com.blogservice.api.repository.post.PostRepository;
import com.blogservice.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.blogservice.api.exception.ErrorCode.*;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostCreate.Response write(Long userId, PostCreate.Request request) {
        User user = findUserById(userId);

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();

        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public PostResponse get(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getList(PostSearch postSearch) {
        return postRepository.getList(postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    public void edit(Long id, PostEdit postEdit) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        post.edit(postEdit);
    }

    public void delete(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);

        postRepository.delete(post);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ServiceException(USER_NOT_FOUND));
    }
}

package com.blogservice.api.repository.post;

import com.blogservice.api.domain.post.Post;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(Long boardId, int page, int size);
}

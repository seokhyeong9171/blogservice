package com.blogservice.api.repository;

import com.blogservice.api.domain.Post;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(int page);
}

package com.blogservice.api.repository;

import com.blogservice.api.domain.Post;
import com.blogservice.api.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}

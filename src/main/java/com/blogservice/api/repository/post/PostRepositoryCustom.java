package com.blogservice.api.repository.post;

import com.blogservice.api.domain.Post;
import com.blogservice.api.request.post.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}

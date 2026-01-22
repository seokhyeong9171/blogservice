package com.blogservice.api.repository.comment;

import com.blogservice.api.dto.CommentDto;

import java.util.List;

public interface CommentRepositoryCustom {

    List<CommentDto.List> findAllParentComments(Long postId, int size, int page);
    List<CommentDto.List> findAllChildComments(Long postId, int size, int page);
}

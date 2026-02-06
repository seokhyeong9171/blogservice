package com.blogservice.api.service;

import com.blogservice.api.dto.BoardResponse;
import com.blogservice.api.repository.board.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public List<BoardResponse.ListResponse> getAllBoards() {
        return boardRepository.findAll().stream().map(BoardResponse.ListResponse::from).toList();
    }
}

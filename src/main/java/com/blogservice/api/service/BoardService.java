package com.blogservice.api.service;

import com.blogservice.api.domain.board.Board;
import com.blogservice.api.dto.BoardResponse;
import com.blogservice.api.exception.ServiceException;
import com.blogservice.api.repository.board.BoardRepository;
import com.blogservice.api.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.blogservice.api.exception.ErrorCode.BOARD_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public List<BoardResponse.List> getAllBoards() {
        return boardRepository.findAll().stream().map(BoardResponse.List::from).toList();
    }

    @Transactional(readOnly = true)
    public BoardResponse.Count getPostCount(Long boardId) {
        Board board = findBoardById(boardId);
        Long count = postRepository.countPostByBoard(board);
        return BoardResponse.Count.from(count);
    }

    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() -> new ServiceException(BOARD_NOT_FOUND));
    }
}

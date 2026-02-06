package com.blogservice.api.controller;

import com.blogservice.api.dto.BoardResponse;
import com.blogservice.api.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<List<BoardResponse.List>> getAllBoards(){

        List<BoardResponse.List> response = boardService.getAllBoards();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{boardId}/count")
    public ResponseEntity<BoardResponse.Count> getPostCount(@PathVariable Long boardId){
        BoardResponse.Count response = boardService.getPostCount(boardId);
        return ResponseEntity.ok(response);
    }

}

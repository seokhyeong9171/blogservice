package com.blogservice.api.controller;

import com.blogservice.api.dto.BoardResponse;
import com.blogservice.api.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<List<BoardResponse.ListResponse>> getAllBoards(){

        List<BoardResponse.ListResponse> response = boardService.getAllBoards();

        return ResponseEntity.ok(response);
    }

}

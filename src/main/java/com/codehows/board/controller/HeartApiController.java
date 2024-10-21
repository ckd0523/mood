package com.codehows.board.controller;

import com.codehows.board.dto.HeartDto;
import com.codehows.board.entity.Board;
import com.codehows.board.entity.User;
import com.codehows.board.service.BoardService;
import com.codehows.board.service.HeartService;
import com.codehows.board.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class HeartApiController {

    private final HeartService heartService;
    private final UserService userService;
    private final BoardService boardService;

    public HeartApiController(HeartService heartService, UserService userService, BoardService boardService) {
        this.heartService = heartService;
        this.userService = userService;
        this.boardService = boardService;
    }

    @PostMapping("/like")
    public ResponseEntity<String> like(@RequestBody HeartDto heartDto) {

        Board board = boardService.findById(heartDto.getBno());
        User user = userService.findByUid(heartDto.getUid());
        boolean result = heartService.toggleHeart(board, user);

        if (result) {
            boardService.incrementheart(board.getBno());
            return ResponseEntity.ok("liked");
        } else {
            boardService.decrementheart(board.getBno());
            return ResponseEntity.ok("x");
        }
    }

    @PostMapping("/like/status")
    @ResponseBody
    public String getLikeStatus(@RequestBody HeartDto heartDto) {
        Board board = boardService.findById(heartDto.getBno());
        User user = userService.findByUid(heartDto.getUid());
        boolean isLiked = heartService.findHeart(board, user);
        return isLiked ? "liked" : "not liked";
    }
}

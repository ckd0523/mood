package com.codehows.board.controller;

import com.codehows.board.dto.AddReplyDto;
import com.codehows.board.entity.Reply;
import com.codehows.board.service.ReplyService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reply")
public class ReplyApiController {

    private final ReplyService replyService;

    @PostMapping
    public ResponseEntity<?> addReply(@RequestBody AddReplyDto request) {
        try {
            Reply savedReply = replyService.save(request);
            JSONObject responseData = new JSONObject();
            responseData.put("bno", savedReply.getBoard().getBno());
            // 기타 필요한 속성들도 마찬가지로 JSON에 추가
            return ResponseEntity.status(HttpStatus.CREATED).body(responseData.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/{bno}")
    public ResponseEntity<?> getRepliesByBoard(@PathVariable Long bno, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "3") int size) {
        try {
            Page<Reply> parentRepliesPage = replyService.findParentRepliesByBnoPaged(bno, page, size);
            List<AddReplyDto> parentReplies = parentRepliesPage.getContent().stream()
                    .map(AddReplyDto::new)
                    .collect(Collectors.toList());

            for (AddReplyDto parentReply : parentReplies) {
                List<Reply> childReplies = replyService.findByParentRno(parentReply.getRno());
                List<AddReplyDto> childReplyDtos = childReplies.stream()
                        .map(AddReplyDto::new)
                        .collect(Collectors.toList());
                parentReply.setChildReplies(childReplyDtos);
            }

            JSONObject responseData = new JSONObject();
            responseData.put("comments", parentReplies);
            responseData.put("hasMore", parentRepliesPage.hasNext());
            responseData.put("currentPage", page);
            responseData.put("totalPages", parentRepliesPage.getTotalPages());

            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }



    @DeleteMapping("/{rno}")
    public ResponseEntity<?> deleteReply(@PathVariable Long rno) {
        try {
            replyService.delete(rno);
            return ResponseEntity.ok("댓글이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PutMapping("/{rno}")
    public ResponseEntity<?> updateReply(@PathVariable Long rno, @RequestBody AddReplyDto request) {
        try {
            replyService.update(rno, request);
            return ResponseEntity.ok("댓글이 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}

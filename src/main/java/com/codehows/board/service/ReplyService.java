package com.codehows.board.service;

import com.codehows.board.dto.AddReplyDto;
import com.codehows.board.entity.Board;
import com.codehows.board.entity.Reply;
import com.codehows.board.entity.User;
import com.codehows.board.repository.ReplyRepository;
import com.codehows.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final BoardService boardService;

    // 댓글 또는 대댓글 등록
    public Reply save(AddReplyDto request) {
        User user = userRepository.findByUid(request.getUid());
        Board board = boardService.findById(request.getBno());
        Reply parentReply = null;

        if (request.getParentRno() != null) {
            parentReply = replyRepository.findById(request.getParentRno())
                    .orElseThrow(() -> new IllegalArgumentException("해당 부모 댓글이 없습니다. id=" + request.getParentRno()));
        }

        Reply reply = new Reply();
        reply.setUser(user);
        reply.setBoard(board);
        reply.setReply(request.getReply());
        reply.setParent(parentReply); // 부모 댓글 설정

        return replyRepository.save(reply);
    }

    public List<Reply> findByBno(Long id){
        return replyRepository.findByBoard_Bno(id);
    }

    @Transactional
    public void delete(Long rno) {
        replyRepository.deleteByRno(rno);
    }

    @Transactional
    public void update(Long rno, AddReplyDto request) throws IllegalAccessException {
        Reply reply = replyRepository.findById(rno)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + rno));
        if (!reply.getUser().getUid().equals(request.getUid())) {
            throw new IllegalAccessException("수정 권한이 없습니다.");
        }
        reply.setReply(request.getReply());
        replyRepository.save(reply);
    }

//    public Page<Reply> findByBnoPaged(Long bno, int page, int size) {
//        Pageable pageable = PageRequest.of(page - 1, size);  // 0-based page index
//        return replyRepository.findByBoard_Bno(bno, pageable);
//    }

    public Page<Reply> findParentRepliesByBnoPaged(Long bno, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size); // 0-based page index
        return replyRepository.findByBoard_BnoAndParentRnoIsNull(bno, pageable);
    }

    public List<Reply> findByParentRno(Long parentRno) {
        return replyRepository.findByParentRno(parentRno);
    }
}

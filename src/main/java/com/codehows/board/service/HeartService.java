package com.codehows.board.service;

import com.codehows.board.entity.Board;
import com.codehows.board.entity.Heart;
import com.codehows.board.entity.HeartId;
import com.codehows.board.entity.User;
import com.codehows.board.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class HeartService {

    final private HeartRepository heartRepository;
    private final BoardService boardService;

    /** 글 좋아요 확인 **/
    public boolean findHeart(Board board, User user) {
        return heartRepository.existsByIdBnoAndIdUid(board, user);
    }


    @Transactional
    public boolean toggleHeart(Board board, User user) {
        try {
            if (heartRepository.existsByIdBnoAndIdUid(board, user)) {

                heartRepository.deleteByIdBnoAndIdUid(board, user);
                // 삭제됨
                return false;
            } else {
                // HeartId 객체 생성 및 설정
                HeartId heartId = new HeartId(board, user);

                // Heart 객체 생성 및 설정
                Heart heart = new Heart();
                heart.setId(heartId);
                // Heart 저장
                heartRepository.save(heart);

                // 추가됨
                return true;
            }
        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
            return false;
        } finally {
            // 트랜잭션 종료
        }
    }
}

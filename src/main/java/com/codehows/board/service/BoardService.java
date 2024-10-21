package com.codehows.board.service;


import com.codehows.board.dto.SearchDto;
import com.codehows.board.entity.AdminBoard;
import com.codehows.board.entity.Board;
import com.codehows.board.entity.Hash;
import com.codehows.board.entity.User;
import com.codehows.board.repository.AdminBoardRepository;
import com.codehows.board.repository.BoardRepository;
import com.codehows.board.repository.HashRepository;
import com.codehows.board.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardService {

    final private BoardRepository boardRepository;
    final private FileService fileService;
    final private UserRepository userRepository;
    final private HashRepository hashRepository;
    final private AdminBoardRepository adminBoardRepository;

    // 모든 게시글 가져오기
    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    public Board findById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));
    }

//    public Board registBoard(BoardRegistDto boardDto, List<String> hashTags, List < MultipartFile > files) throws IOException {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//
//        System.out.println("userName 출력!!!!!!" + username);
//
//        User user = userRepository.findByUid(username);
//
//        Board board = new Board();
//        board.setBno(boardDto.getBno());
//        board.setTitle(boardDto.getTitle());
//        board.setContent(boardDto.getContent());
//        board.setHit(0L);
//        board.setHeart(0L);
//        board.setWriter(userRepository.findByUid(SecurityContextHolder.getContext().getAuthentication().getName()));
//        board = boardRepository.save(board);
//        //첨부파일
//        for (MultipartFile multipartFile : files) {
//            fileService.saveFile(multipartFile,board.getBno());
//        }
//        //해시태그 저장 , 빈칸일시 데이터베이스에 저장 안되도록 설정
//        if (hashTags != null && !hashTags.isEmpty()) {
//            for (String tag : hashTags) {
//                if (tag != null && !tag.trim().isEmpty()) {
//
//
//                    String tagWithoutHash = tag.startsWith("#") ? tag.substring(1) : tag;
//
//                    Hash hashTag = new Hash();
//                    hashTag.setHash(tagWithoutHash);
//                    hashTag.setBno(board);
//                    hashRepository.save(hashTag);
//                }
//            }
//        }
//        return board;
//    }

    @Transactional
    public void incrementHit(Long bno) {
        // 게시글 번호로 게시글을 조회하여 조회수를 증가시킴
        Board board = boardRepository.findById(bno)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없음"));
        board.incrementHit();
        boardRepository.save(board);
    }

    @Transactional
    public void incrementheart(Long bno) {
        // 게시글 번호로 게시글을 조회하여 조회수를 증가시킴
        Board board = boardRepository.findById(bno)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없음"));
        board.incrementheart();
        boardRepository.save(board);
    }

    @Transactional
    public void decrementheart(Long bno) {
        // 게시글 번호로 게시글을 조회하여 조회수를 증가시킴
        Board board = boardRepository.findById(bno)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없음"));
        board.decrementheart();
        boardRepository.save(board);
    }

//    @Transactional
//    public Board updateBoard(Long bno, BoardRegistDto boardDto, List<String> hashTags, List<MultipartFile> files) throws IOException {
//        Board board = boardRepository.findById(bno).orElseThrow(() -> new IllegalArgumentException("Invalid board Id: " + bno));
//        board.setTitle(boardDto.getTitle());
//        board.setContent(boardDto.getContent());
//        // 새로운 파일이 제공된 경우에만 기존 파일 삭제
//
//            for (MultipartFile multipartFile : files) {
//                fileService.saveFile(multipartFile, board.getBno());
//            }
//
//        // 해시태그 업데이트
//        hashRepository.deleteByBno(board);
//        if (hashTags != null && !hashTags.isEmpty()) {
//            for (String tag : hashTags) {
//                if (tag != null && !tag.trim().isEmpty()) {
//                    String tagWithoutHash = tag.startsWith("#") ? tag.substring(1) : tag;
//                    Hash hashTag = new Hash();
//                    hashTag.setHash(tagWithoutHash);
//                    hashTag.setBno(board);
//                    hashRepository.save(hashTag);
//                }
//            }
//        }
//        return boardRepository.save(board);
//    }

    public Board restoreBoard(Long Bno){
        //bno로 AdminBoardRepo -> 조회
        AdminBoard adminBoard = adminBoardRepository.findById(Bno)
                .orElseThrow(EntityNotFoundException::new);

        //Board 엔티티로 매핑해서 추가


        User user = userRepository.findByUid(adminBoard.getWriter());

        Board board = new Board();
        board.setTitle(adminBoard.getTitle());
        board.setContent(adminBoard.getContent());
        board.setHit(0L);
        board.setHeart(0L);
        board.setWriter(user);
        board = boardRepository.save(board);

        //AdminBoard table에서 adminBoard를 삭제
        adminBoardRepository.deleteById(Bno);

        return board;
    }

    //게시글 삭제
    @Transactional
    public void deleteBoard(Long bno) {
        Board board = boardRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id: " + bno));
        boardRepository.delete(board);
    }

    @Transactional(readOnly = true)
    public Page<Board> getBoardPage(SearchDto searchDto, String sortType, String direction, Pageable pageable){
        return boardRepository.getBoardPage(searchDto, sortType, direction, pageable);
    }

    public Board getBoardById(Long bno) {
        return boardRepository.findById(bno).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. : " + bno));
    }

    public Long getBoardCount(){
        return boardRepository.countBoardByCount();
    }

}

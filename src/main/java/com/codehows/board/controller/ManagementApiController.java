package com.codehows.board.controller;

import com.codehows.board.dto.*;
import com.codehows.board.entity.*;
import com.codehows.board.repository.*;
import com.codehows.board.service.BoardService;
import com.codehows.board.service.FileService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/board/admin/api")
public class ManagementApiController {

    private final AdminBoardRepository adminBoardRepository;
    private final BoardService boardService;
    private final HashRepository hashRepository;
    private final AdminHashRepository adminHashRepository;
    private final FileService fileService;
    private final AdminFileRepository adminFileRepository;
    private final BoardRepository boardRepository;
    private final FileManagementRepository fileManagementRepository;

    @PostMapping("/add/{bno}")
    public ResponseEntity<?> addAdminBoard(@PathVariable Long bno) {
        try {
            Board board = boardService.getBoardById(bno);

            AdminBoard adminBoard = new AdminBoard();
            adminBoard.setBno(board.getBno());
            adminBoard.setWriter(board.getWriter().getUid());
            adminBoard.setTitle(board.getTitle());
            adminBoard.setContent(board.getContent());
            adminBoardRepository.save(adminBoard);

            // Hash 복사
            List<Hash> hashes = hashRepository.findByBno(board);
            for (Hash hash : hashes) {
                AdminHash adminHash = new AdminHash();
                adminHash.setBno(hash.getBno().getBno());
                adminHash.setHash(hash.getHash());
                adminHash.setHno(hash.getHno());
                adminHashRepository.save(adminHash);
            }

            // File 복사
            List<FileEntity> files = fileService.findFilesByBoard(board);
            for (FileEntity fileEntity : files) {
                AdminFile adminFile = new AdminFile();
                adminFile.setBno(fileEntity.getBoard().getBno());
                adminFile.setFno(fileEntity.getFno());
                adminFile.setFileName(fileEntity.getFileName());
                adminFile.setFileUrl(fileEntity.getFileUrl());
                adminFile.setFileOrigin(fileEntity.getFileOrigin());
                adminFileRepository.save(adminFile);
            }

            return ResponseEntity.ok("게시글 복사 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 복사 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    //복원 기능
    @PostMapping("/board/restore/{Bno}")
    public ResponseEntity<String> restoreBoard(@PathVariable Long Bno) {

        boardService.restoreBoard(Bno);
        return ResponseEntity.ok("게시글이 복원되었습니다.");
    }

    @GetMapping(value = "/dashboard2")
    public String dashboard2(Model model){


        // 장르별 좋아요수
//        List<Object[]> genrelike = boardRepository.getTotalLikesPerGenre();
//        model.addAttribute("genrelike", genrelike);
//        // 장르별 방문자수
//        List<Object[]> genrevitsit = boardRepository.getTotalVisitsPerGenre();
//        model.addAttribute("genrevitsit", genrevitsit);
        // 좋아요가 가장 많은 게시글
//        List<Board> mostLikedBoard = boardRepository.getMostLikedBoard();
//        model.addAttribute("mostLikedBoard", mostLikedBoard);
//        // 방문자가 가장 많은 게시글
//        List<Board> mostVisitedBoard = boardRepository.getMostVisitedBoard();
//        model.addAttribute("mostVisitedBoard", mostVisitedBoard);

        return "admin/dashboard2";
    }

    // 장르별 좋아요수
    @GetMapping("/dashboard2/data")
    public ResponseEntity<?> getDashboardData() {
        List<Object[]> data = boardRepository.getTotalLikesPerGenre();

        List<Map<String, Object>> chartData = new ArrayList<>();
        for (Object[] row : data) {
            Map<String, Object> item = new HashMap<>();
            String genre = (String) row[0];
            BigDecimal totalLikes = (BigDecimal) row[1];
            item.put("name", genre);
            item.put("data", totalLikes.longValue()); // BigDecimal을 long으로 변환하여 저장
            chartData.add(item);
        }

        return ResponseEntity.ok(chartData);
    }
    // 장르별 방문자수
    @GetMapping("/dashboard2/data2")
    public ResponseEntity<?> getDashboardData2() {
        List<Object[]> data = boardRepository.getTotalVisitsPerGenre();

        List<Map<String, Object>> chartData = new ArrayList<>();
        for (Object[] row : data) {
            Map<String, Object> item = new HashMap<>();
            String genre = (String) row[0];
            BigDecimal total_visits = (BigDecimal) row[1];
            item.put("name", genre);
            item.put("data", total_visits.longValue()); // BigDecimal을 long으로 변환하여 저장
            chartData.add(item);
        }

        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/fileManagement")
    public List<String> getFileExtensions() {
        return fileManagementRepository.findAll()
                .stream()
                .map(FileManagement::getExtension)
                .collect(Collectors.toList());
    }

    @PostMapping("/fileManagement")
    public String saveFileExtensions(@RequestBody List<String> extensions) {
        // 기존 확장자 모두 삭제
        fileManagementRepository.deleteAll();

        // 새로운 확장자 추가
        for (String ext : extensions) {
            FileManagement fileManagement = new FileManagement();
            fileManagement.setExtension(ext);
            fileManagementRepository.save(fileManagement);
        }
        return "Extensions saved successfully";
    }



}

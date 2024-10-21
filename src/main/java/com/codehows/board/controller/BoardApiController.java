package com.codehows.board.controller;

import com.codehows.board.dto.BoardRegist;

import com.codehows.board.dto.FileDto;
import com.codehows.board.entity.Board;
import com.codehows.board.entity.FileEntity;
import com.codehows.board.entity.Hash;
import com.codehows.board.entity.User;
import com.codehows.board.repository.BoardRepository;
import com.codehows.board.repository.FileRepository;
import com.codehows.board.repository.HashRepository;
import com.codehows.board.repository.UserRepository;
import com.codehows.board.service.BoardService;
import com.codehows.board.service.UserService;
import jakarta.validation.Valid;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BoardApiController {

    private final BoardRepository boardRepository;
    private final UserService userService;
    private final HashRepository hashRepository;
    private final FileRepository fileRepository;
//    private final BoardService boardService;


    public BoardApiController(UserService userService, BoardRepository boardRepository, HashRepository hashRepository, FileRepository fileRepository) {
        this.userService = userService;
        this.boardRepository = boardRepository;
        this.hashRepository = hashRepository;
        this.fileRepository = fileRepository;
//        this.boardService = boardService;
    }


    @PostMapping("/board")
    public ResponseEntity<?> registApiBoard(@RequestBody BoardRegist formData) throws IOException {
        // title, content, hashTags를 이용하여 원하는 작업을 수행
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUid(username);

        Board board = new Board();
        board.setTitle(formData.getTitle());
        board.setContent(formData.getContent());
        board.setHit(0L);
        board.setHeart(0L);
        board.setGenre(formData.getGenre());
        board.setWriter(user);
        board = boardRepository.save(board);

        // 해시태그 저장
        if (formData.getHashTags() != null && !formData.getHashTags().isEmpty()) {
            for (String tag : formData.getHashTags()) {
                if (tag != null && !tag.trim().isEmpty()) {
                    String tagWithoutHash = tag.startsWith("#") ? tag.substring(1) : tag;

                    Hash hashTag = new Hash();
                    hashTag.setHash(tagWithoutHash);
                    hashTag.setBno(board);
                    hashRepository.save(hashTag);
                }
            }
        }

        // 파일 저장
        for (FileDto file : formData.getFiles()) {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setBoard(board);
            fileEntity.setFileOrigin(file.getFileOrigin());
            fileEntity.setFileName(file.getFileName());
            fileEntity.setFileUrl(file.getFileUrl());
            fileRepository.save(fileEntity);
        }

        // 원하는 작업 후 데이터를 담은 객체를 반환
        return ResponseEntity.ok().body(null);
    }

    //게시글 수정
    @PostMapping("board/edit/{bno}")
    public ResponseEntity<?> editBoard(@PathVariable Long bno, @RequestBody BoardRegist formData) {
        Board board = boardRepository.findById(bno).orElseThrow(() -> new IllegalArgumentException("Invalid board Id: " + bno));
        board.setTitle(formData.getTitle());
        board.setContent(formData.getContent());
        board.setGenre(formData.getGenre());

            // 해시태그 업데이트
            hashRepository.deleteByBno(board);
            if (formData.getHashTags() != null && !formData.getHashTags().isEmpty()) {
                for (String tag : formData.getHashTags()) {
                    if (tag != null && !tag.trim().isEmpty()) {
                        String tagWithoutHash = tag.startsWith("#") ? tag.substring(1) : tag;
                        Hash hashTag = new Hash();
                        hashTag.setHash(tagWithoutHash);
                        hashTag.setBno(board);
                        hashRepository.save(hashTag);
                    }
                }
            }

        if (formData.getFiles() != null) {
            for (FileDto file : formData.getFiles()) {
                FileEntity fileEntity = new FileEntity();
                fileEntity.setBoard(board);
                fileEntity.setFileOrigin(file.getFileOrigin());
                fileEntity.setFileName(file.getFileName());
                fileEntity.setFileUrl(file.getFileUrl());
                fileRepository.save(fileEntity);
            }
        }

        boardRepository.save(board);
        return ResponseEntity.ok().body(null);
    }

}

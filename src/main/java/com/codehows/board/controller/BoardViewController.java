package com.codehows.board.controller;


import com.codehows.board.dto.BoardRegistDto;
import com.codehows.board.dto.SearchDto;
import com.codehows.board.entity.Board;
import com.codehows.board.entity.FileEntity;
import com.codehows.board.entity.Hash;
import com.codehows.board.entity.User;
import com.codehows.board.repository.FileRepository;
import com.codehows.board.repository.HashRepository;
import com.codehows.board.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BoardViewController {

    private final BoardService boardService;
    private final UserService userService;
    private final HashRepository hashRepository;
    private final HeartService heartService;
    private final FileService fileService;
    private final FileRepository fileRepository;
    private final VisitorServiceImpl visitorService;


    // 목록 (현민)
    @GetMapping(value = {"/boardList/{page}", "/boardList", "/"})
    public String boardList(@RequestParam(value = "sortType", defaultValue = "") String sortType,
                            @RequestParam(value = "direction", defaultValue = "asc") String direction,
                            Model model, SearchDto searchDto,
                            @PathVariable("page") Optional<Integer> page) {

        User user = new User();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            user = userService.findByUid(username);
            model.addAttribute("user", user);
        } catch (Exception e) {
            user.setUid("비회원");
            model.addAttribute("user", user);
        }

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortOrder;
        switch (sortType) {
            case "title":
                sortOrder = Sort.by(sortDirection, "title");
                break;
            case "writer":
                sortOrder = Sort.by(sortDirection, "writer");
                break;
            case "hash":
                sortOrder = Sort.by(sortDirection, "hash");
                break;
            case "bno":
            default:
                sortOrder = Sort.by(sortDirection, "bno");
                break;
        }

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10, sortOrder);
        System.out.println("Page Size: " + pageable.getPageSize());
        Page<Board> boards = boardService.getBoardPage(searchDto, sortType, direction, pageable);
        System.out.println("Returned Boards: " + boards.getContent().size());

        // 확인을 위한 디버깅 로그 추가
        for (Board board : boards.getContent()) {
            System.out.println("Board: " + board.getTitle());
        }

        List<List<Hash>> boardHashes = new ArrayList<>();
        for (Board board : boards.getContent()) {
            List<Hash> hashes = hashRepository.findByBno(board);
            boardHashes.add(hashes);
        }
        visitorService.incrementVisitorCount(user.getUid());
        model.addAttribute("boards", boards);
        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());
        model.addAttribute("hasNext", boards.hasNext());
        model.addAttribute("hasPrev", boards.hasPrevious());
        model.addAttribute("searchDto", searchDto);
        model.addAttribute("boardHashes", boardHashes);
        model.addAttribute("maxPage", 5);
        return "board/boardList";
    }


    // 게시글 등록 (두일)
//    @PostMapping("/board")
//    public String registBoard(@Valid @ModelAttribute("board") BoardRegistDto boardDto,BindingResult result ,@RequestParam("hashTags") List<String> hashTags, @RequestParam("uploadFiles") List<MultipartFile> files,Model model) throws IOException {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        User user = userService.findByUid(username);
//        if(result.hasErrors()) {
//            System.out.println("에러 확인 완료");
//
//            model.addAttribute("user", user);
//            model.addAttribute("board", boardDto);
//            return "board/boardRegistForm";
//        }
//        boardService.registBoard(boardDto, hashTags, files);
//        return "redirect:/boardList";
//    }

    @GetMapping("board/boardRegistForm")
    public String createBoard(Model model) {

        //User user = new User();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUid(username);
        model.addAttribute("user", user);

        model.addAttribute("board",new BoardRegistDto());
        System.out.println("전달완료");
        return "board/boardRegistForm";
    }

    //게시글 수정
    @GetMapping("board/modify/{bno}")
    public String showModifyForm(@PathVariable Long bno, Model model) {
        Board board = boardService.getBoardById(bno);
        BoardRegistDto boardDto = new BoardRegistDto();
        List<Hash> hashes = hashRepository.findByBno(board);
        List<FileEntity> files = fileRepository.findByBoardBno(bno);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUid(username);
        model.addAttribute("user", user);
        boardDto.setBno(board.getBno());
        boardDto.setTitle(board.getTitle());
        boardDto.setContent(board.getContent());

        model.addAttribute("board", board);
        model.addAttribute("boardDto", boardDto);
        model.addAttribute("hashes", hashes);
        model.addAttribute("files" ,files);

        return "board/boardUpdateForm";
    }

    //게시글 삭제
    @PostMapping("/board/delete")
    public String deleteBoard(Long bno){
        boardService.deleteBoard(bno);
        return "redirect:/boardList";
    }


    // 게시글 상세 화면 (창현)
    @GetMapping("/board/{bno}")
    public String getBoard(@PathVariable Long bno, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 사용자 정보 조회
        String username = authentication.getName();
        User user = userService.findByUid(username);
        Board board = boardService.findById(bno);
        boardService.incrementHit(bno);
        List<Hash> hashes = hashRepository.findByBno(board);
        boolean heart = heartService.findHeart(board, user);
        List<FileEntity> files = fileService.findFilesByBoard(board);

        model.addAttribute("user", user); // 아이디
        model.addAttribute("currentUser", username);  //0521
        model.addAttribute("board", new BoardRegistDto(board));
        model.addAttribute("heart", heart);
        model.addAttribute("files", files);
        model.addAttribute("hashes", hashes);
        return "board/detail_board";
    }
}

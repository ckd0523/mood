package com.codehows.board.controller;


import com.codehows.board.dto.ChartResponse;
import com.codehows.board.dto.SearchDto;
import com.codehows.board.entity.AdminBoard;
import com.codehows.board.entity.Board;
import com.codehows.board.entity.Hash;
import com.codehows.board.entity.User;
import com.codehows.board.repository.BoardRepository;
import com.codehows.board.repository.HashRepository;
import com.codehows.board.service.AdminboardService;
import com.codehows.board.service.BoardService;
import com.codehows.board.service.UserService;
import com.codehows.board.service.VisitorServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ManagementController {

    private final BoardService boardService;
    private final UserService userService;
    private final AdminboardService adminboardService;
    private final BoardRepository boardRepository;

    @GetMapping(value =  "/admin/boardManagement")
    public String adminBoardList(Model model) {

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

        List<AdminBoard> adminBoards = adminboardService.findAll();

        model.addAttribute("adminBoards", adminBoards);


        return "admin/boardManagement";
    }

    private final VisitorServiceImpl visitorService;

    @GetMapping(value = "/admin/dashboard")
    public String dashboard(Model model){

        User user = new User();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            user = userService.findByUid(username);
            model.addAttribute("user", user);
        } catch (Exception e){
            user.setUid("비회원");
            model.addAttribute("user", user);
        }
        //전체 방문자수
        Long totalVisitorCount = visitorService.getTotalVisitorCount();
        //금일 방문자수
        Long todayVisitorCount = visitorService.getTodayVisitorCount();
        Long boardCount = boardService.getBoardCount();
        Long userCount = userService.getTotalUserCount();


        List<Long> lastWeekVisitorCounts = visitorService.getLastWeekVisitorsCounts();
        model.addAttribute("visitData", lastWeekVisitorCounts);
        model.addAttribute("total", totalVisitorCount);
        model.addAttribute("today", todayVisitorCount);
        model.addAttribute("boardCount" , boardCount);
        model.addAttribute("userCount", userCount);

        return "admin/dashboard";
    }

    @GetMapping(value = "/admin/dashboard2")
    public String dashboard2(Model model){

        User user = new User();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            user = userService.findByUid(username);
            model.addAttribute("user", user);
        } catch (Exception e){
            user.setUid("비회원");
            model.addAttribute("user", user);
        }

        //좋아요가 가장 많은 게시글
        List<Board> mostLikedBoard = boardRepository.getMostLikedBoard();
        model.addAttribute("mostLikedBoard", mostLikedBoard);
        // 방문자가 가장 많은 게시글
        List<Board> mostVisitedBoard = boardRepository.getMostVisitedBoard();
        model.addAttribute("mostVisitedBoard", mostVisitedBoard);
        
        return "admin/dashboard2";
    }

    @GetMapping(value = "/admin/fileManagement")
    public String FileManagement(Model model){

        User user = new User();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            user = userService.findByUid(username);
            model.addAttribute("user", user);
        } catch (Exception e){
            user.setUid("비회원");
            model.addAttribute("user", user);
        }

        return "admin/FileManagement";
    }
    
}

package com.codehows.board.controller;

import com.codehows.board.dto.AddUserRequest;
import com.codehows.board.entity.User;
import com.codehows.board.repository.UserRepository;
import com.codehows.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserViewController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String type) {
        if ("kakao".equals(type)) {
            // Kakao login logic
            return "kakaoLogin";
        } else {
            // Default user login logic
            return "login";
        }
    }

    @GetMapping("/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "login";
    }

    @GetMapping("/newUser")
    public String signup(){
        return "newUser";
    }

    @PostMapping("/user/deleteAccount")
    public ResponseEntity<Map<String, String>> deleteUser(@RequestBody Map<String, String> payload) {
        Map<String, String> response = new HashMap<>();
        try {
            String uid = payload.get("uid");
            String password = payload.get("password");
            System.out.println("현재 uid !: " + uid + ", Password: " + password);  // 로그 추가
            User user = userService.findByUid(uid);
            if(user ==null) {
                response.put("message", "유저를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!userService.isPasswordValid(user, password)) {
                response.put("message", "Invalid password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            userService.deleteMember(user);
            response.put("message", "회원탈퇴가 완료되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "에러: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(value = "/admin/userManagement")
    public String userManage(Model model){

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
        List<User> users = userService.findAll();

        model.addAttribute("users", users);
        return "admin/userManagement";
    }
}
package com.codehows.board.controller;

import com.codehows.board.dto.AddUserRequest;
import com.codehows.board.entity.User;
import com.codehows.board.repository.UserRepository;
import com.codehows.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/user")
    public String signup(AddUserRequest request) {
        userService.save(request);
        return "redirect:/login";
    }


    @PostMapping("/user/checkuid")
    @ResponseBody
    public ResponseEntity<?> checkUid(@RequestParam("uid") String uid) {

        Map<String, String> response = new HashMap<>();
        User confirmUser = userRepository.findByUid(uid);
        if (confirmUser != null) {
            response.put("message", "이미 존재하는 아이디입니다.");
        } else {
            response.put("message", "사용 가능한 아이디입니다.");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/checknick")
    @ResponseBody
    public ResponseEntity<?> checkNick(@RequestParam("nickname") String nickname) {

        Map<String, String> response = new HashMap<>();
        User confirmUser = userRepository.findByNickname(nickname);
        System.out.println("닉네임 받아오기 성공 " + confirmUser);
        if (confirmUser != null) {
            response.put("message", "이미 존재하는 닉네임입니다.");
        } else {
            response.put("message", "사용 가능한 닉네임입니다.");
        }
        return ResponseEntity.ok(response);
    }
}

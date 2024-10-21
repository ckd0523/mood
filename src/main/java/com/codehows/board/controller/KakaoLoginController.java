package com.codehows.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KakaoLoginController {

    @GetMapping("/kakao/callback")
    public String kakaoCallback() {
        // 사용자 정보를 받아와서 처리 (예: 회원가입 또는 로그인 처리)
        return "redirect:/home";
    }
}

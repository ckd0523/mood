package com.codehows.board.config.oauth;

import com.codehows.board.entity.User;
import com.codehows.board.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MyOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            System.out.println(new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));

        }catch (Exception e) {
            e.printStackTrace();
        }

        String uid ="kakao_"+ oAuth2User.getAttributes().get("id");

        Map<String,String> responseMap = (Map<String,String>) oAuth2User.getAttributes().get("kakao_account");

        Map<String,String> properties = (Map<String,String>) oAuth2User.getAttributes().get("properties");
        String userNickname = properties.get("nickname");

        String email ="kakao_"+responseMap.get("email");

        User user = new User(uid,email,userNickname);

        userRepository.save(user);

        return new CustomOAuth2User(uid);
    }
}
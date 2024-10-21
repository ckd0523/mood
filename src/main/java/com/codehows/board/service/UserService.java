package com.codehows.board.service;

import com.codehows.board.dto.AddUserRequest;
import com.codehows.board.entity.User;
import com.codehows.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User save(AddUserRequest request){
        return userRepository.save(User.builder()
                .uid(request.getUid())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .email(request.getEmail())
                .build());
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findByUid(String uid){
        return userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    public void deleteMember(User user) {

        userRepository.delete(user);
    }

    public boolean isPasswordValid(User user, String realPassword) {
        return bCryptPasswordEncoder.matches(realPassword, user.getPassword());

    }

    public Long getTotalUserCount(){
        return userRepository.countUserByCount();
    }
}

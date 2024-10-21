package com.codehows.board.service;

import com.codehows.board.entity.AdminBoard;
import com.codehows.board.repository.AdminBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminboardService {

    final AdminBoardRepository adminBoardRepository;

    public List<AdminBoard> findAll(){
        return adminBoardRepository.findAll();
    }

}

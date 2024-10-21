package com.codehows.board.repository;

import com.codehows.board.dto.SearchDto;
import com.codehows.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {

    Page<Board> getBoardPage(SearchDto searchDto, String sortType, String direction, Pageable pageable);
}


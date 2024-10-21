package com.codehows.board.repository;


import com.codehows.board.entity.AdminBoard;
import com.codehows.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminBoardRepository extends JpaRepository<AdminBoard, Long> {

    AdminBoard save(AdminBoard adminBoard);

    List<AdminBoard> findByBno(Board board);

}

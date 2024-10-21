package com.codehows.board.repository;

import com.codehows.board.entity.Board;
import com.codehows.board.entity.FileEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    @Query("SELECT f FROM FileEntity f WHERE f.board.bno = :bno")
    List<FileEntity> findByBoardBno(Long bno);

    @Modifying
    @Transactional
    @Query("DELETE FROM FileEntity f WHERE f.board.bno = :bno")
    void deleteByBoardBno(Long bno);

    List<FileEntity> findByBoard(Board board);
}

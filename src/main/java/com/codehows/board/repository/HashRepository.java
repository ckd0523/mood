package com.codehows.board.repository;

import com.codehows.board.entity.Board;
import com.codehows.board.entity.Hash;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    List<Hash> findByBno(Board board);

    @Modifying
    @Transactional
    @Query("DELETE FROM Hash h WHERE h.bno = :board")
    void deleteByBno(Board board);

}

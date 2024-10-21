package com.codehows.board.repository;

import com.codehows.board.entity.AdminFile;
import com.codehows.board.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminFileRepository extends JpaRepository<FileEntity, Long> {

    @Query("SELECT f FROM FileEntity f WHERE f.board.bno = :bno")
    List<FileEntity> findByBoardBno(Long bno);

    AdminFile save(AdminFile adminFile);

}

package com.codehows.board.repository;

import com.codehows.board.entity.FileManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileManagementRepository extends JpaRepository<FileManagement, String> {
}

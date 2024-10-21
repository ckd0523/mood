package com.codehows.board.repository;

import com.codehows.board.entity.Board;
import com.codehows.board.entity.Heart;
import com.codehows.board.entity.HeartId;
import com.codehows.board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeartRepository extends JpaRepository<Heart, HeartId> {

    boolean existsByIdBnoAndIdUid(Board bno, User uid);

    void deleteByIdBnoAndIdUid(Board bno, User uid);
}

package com.codehows.board.repository;

import com.codehows.board.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long>, VisitorRepositoryCustom {

    @Query(value = "select v " +
            " from Visitor v" +
            " where v.uid = :uid and v.regDate >= :startOfDay And v.regDate < :endOfDay")
    Optional<Visitor> findByUidAndRegDateBetween(@Param("uid") String uid, @Param("startOfDay")LocalDateTime startOfDay, @Param("endOfDay")LocalDateTime endOfDay);

    Long countBy();

    @Query(value = " select count(*)" +
            " from Visitor v " +
            " where v.regDate >= :startOfDay And v.regDate < :endOfDay")
    Long countByTodayCount(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

//
//    @Query("SELECT COUNT(v) FROM Visitor v WHERE v.regDate >= :startOfDay AND v.regDate < :endOfDay")
//    List<Long> countVisitorForLastWeek(LocalDateTime startDate, LocalDateTime endDate);

}

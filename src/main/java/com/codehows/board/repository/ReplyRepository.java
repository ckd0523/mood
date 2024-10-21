package com.codehows.board.repository;

import com.codehows.board.entity.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("SELECT r FROM Reply r WHERE r.board.bno = :bno")
    List<Reply> findByBoard_Bno(@Param("bno") Long bno);

    void deleteByRno(Long rno);

//    Page<Reply> findByBoard_Bno(Long bno, Pageable pageable);

    Page<Reply> findByBoard_BnoAndParentRnoIsNull(Long bno, Pageable pageable);
    List<Reply> findByParentRno(Long parentRno);

}
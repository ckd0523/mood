package com.codehows.board.repository;

import com.codehows.board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUid(String uid);
    User findByNickname(String nickname);

    @Query(value = "select count(*) " +
            " from User u")
    Long countUserByCount();

}

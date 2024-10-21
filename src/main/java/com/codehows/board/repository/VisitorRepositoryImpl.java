package com.codehows.board.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VisitorRepositoryImpl implements VisitorRepositoryCustom{
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Long> countVisitorForLastWeek(LocalDateTime startDate, LocalDateTime endDate) {

        List<Long> dailyVisitorCounts = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDateTime startOfDay = startDate.plusDays(i);
            LocalDateTime endOfDay = startOfDay.plusDays(1);

            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(v) FROM Visitor v WHERE v.regDate >= :startOfDay AND v.regDate < :endOfDay", Long.class);
            query.setParameter("startOfDay", startOfDay);
            query.setParameter("endOfDay", endOfDay);

            Long count = query.getSingleResult();
            dailyVisitorCounts.add(count);
        }

        return dailyVisitorCounts;
    }
}

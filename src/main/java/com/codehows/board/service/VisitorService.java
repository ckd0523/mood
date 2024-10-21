package com.codehows.board.service;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitorService {

    void incrementVisitorCount(final String uid);

    Boolean CheckTodayVisit(String uid, LocalDateTime startOfDay, LocalDateTime endOfDay);

    Long getTotalVisitorCount();

    Long getTodayVisitorCount();

    List<Long> getLastWeekVisitorsCounts();
}

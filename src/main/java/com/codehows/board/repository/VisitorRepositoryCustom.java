package com.codehows.board.repository;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitorRepositoryCustom {

    List<Long> countVisitorForLastWeek(LocalDateTime startDate, LocalDateTime endDate);
}

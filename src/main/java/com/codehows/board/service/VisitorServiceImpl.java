package com.codehows.board.service;

import com.codehows.board.entity.Visitor;
import com.codehows.board.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService{

    private final VisitorRepository visitorRepository;
    @Override
    public void incrementVisitorCount(String uid) {
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        if(!(CheckTodayVisit(uid, startOfDay, endOfDay))){
            Visitor visitor = new Visitor();
            visitor.setUid(uid);
            visitor.setRegDate(date);
            visitorRepository.save(visitor);
        }
    }

    @Override
    public Boolean CheckTodayVisit(String uid, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        Optional<Visitor> byUidAndRegDateLike = visitorRepository.findByUidAndRegDateBetween(uid, startOfDay, endOfDay);

        return byUidAndRegDateLike.isPresent();
    }

    @Override
    public Long getTotalVisitorCount() {
        return visitorRepository.countBy();
    }

    @Override
    public Long getTodayVisitorCount() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return visitorRepository.countByTodayCount(startOfDay, endOfDay);
    }

    @Override
    public List<Long> getLastWeekVisitorsCounts() {

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime startOfLastWeek = now.minusDays(6);

        return visitorRepository.countVisitorForLastWeek(startOfLastWeek,now.plusDays(1));
    }
}

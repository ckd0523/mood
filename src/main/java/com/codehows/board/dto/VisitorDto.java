package com.codehows.board.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString

public class VisitorDto {
    Long TotalVisitorCount;
    Long TodayVisitorCount;
    String uid;
}

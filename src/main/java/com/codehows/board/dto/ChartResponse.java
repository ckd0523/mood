package com.codehows.board.dto;

import com.codehows.board.entity.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class ChartResponse {
    private String name;
    private Long data;

    public ChartResponse(String genre, Long data) {
        this.name = genre;
        this.data = data;
    }

}

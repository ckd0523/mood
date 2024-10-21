package com.codehows.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name="AdminBoard")
public class AdminBoard {

    @Id
    @Column(name = "bno")
    private Long bno;

    private String writer;

    private String title;

    private String content;

}

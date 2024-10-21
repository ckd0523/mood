package com.codehows.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@Getter
@Setter
@ToString
@Embeddable
public class HeartId implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "bno", referencedColumnName = "bno")
    private Board bno;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    private User uid;

    public HeartId(Board board, User user) {
        this.bno = board;
        this.uid = user;
    }

    public HeartId() {
    }
}

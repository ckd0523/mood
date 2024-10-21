package com.codehows.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@ToString
@Table(name="AdminHash")
public class AdminHash {

    @Id
    @Column(name="hno")
    private Long hno;

    private String hash;

    private Long bno;

}

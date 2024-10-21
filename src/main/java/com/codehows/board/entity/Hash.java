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
@Table(name="hash")
public class Hash {

    @Id
    @Column(name="hno")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hno;

    private String hash;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="bno", referencedColumnName = "bno")
    private Board bno;
}
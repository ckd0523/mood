package com.codehows.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name="AdminFile")
public class AdminFile {

    @Id
    @Column(name="fno")
    private Long fno;

    private Long bno;

    private String fileUrl;

    private String fileName;

    private String fileOrigin;

}

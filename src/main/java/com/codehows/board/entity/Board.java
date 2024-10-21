package com.codehows.board.entity;

import com.codehows.board.dto.BoardRegistDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name="board")
public class Board extends BaseTimeEntity {

    @Id
    @Column(name = "bno")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    //(cascade = CascadeType.ALL)
    @JoinColumn(name="uid", referencedColumnName = "uid")
    private User writer;

    private String title;

    @Column(columnDefinition = "varchar(10000)")
    private String content;

    private String genre;

    private Long hit;

    private Long heart;


    public void incrementheart() {this.heart++;}

    public void decrementheart() {this.heart--;}
    //클릭시 조회수 증가
    public void incrementHit() {
        this.hit++;
    }

}
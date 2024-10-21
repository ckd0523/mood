package com.codehows.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name="file")
public class FileEntity {

    @Id
    @Column(name="fno")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fno;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="bno")
    private Board board;

    private String fileUrl;

    private String fileName;

    private String fileOrigin;

    @Builder
    public FileEntity(Long fno, String fileUrl, String fileName, String fileOrigin) {
        this.fno = fno;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileOrigin = fileOrigin;
    }
}

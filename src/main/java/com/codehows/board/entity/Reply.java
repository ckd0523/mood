package com.codehows.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@ToString
@Table(name="reply")
@NoArgsConstructor
@AllArgsConstructor
public class Reply {

    @Id
    @Column(name="rno")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="bno")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="uid")
    private User user;

    private String reply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_rno")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Reply parent;

    // Constructor without parent for normal comments
    public Reply(Board board, User user, String reply) {
        this.board = board;
        this.user = user;
        this.reply = reply;
    }

    // Constructor with parent for replies
    public Reply(Board board, User user, String reply, Reply parent) {
        this.board = board;
        this.user = user;
        this.reply = reply;
        this.parent = parent;
    }

}

package com.codehows.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Visitor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vid;

    @CreatedDate
    private LocalDateTime regDate;

    private String uid;
}

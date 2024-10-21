package com.codehows.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name="fileManagement")
public class FileManagement {
    @Id
    @Column(name="Extension")
    private String extension;
}

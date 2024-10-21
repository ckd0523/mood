package com.codehows.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class FileDto {
    String fileName;
    String fileOrigin;
    String fileUrl;

}

package com.codehows.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BoardRegist {

    private String title;
    private String content;
    private List<String> hashTags;
    private String genre;
    private List<FileDto> files;

}

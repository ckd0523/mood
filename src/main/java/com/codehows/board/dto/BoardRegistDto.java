package com.codehows.board.dto;

import com.codehows.board.entity.AdminBoard;
import com.codehows.board.entity.BaseTimeEntity;
import com.codehows.board.entity.Board;
import com.codehows.board.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BoardRegistDto extends BaseTimeEntity {

    private Long bno;
    private User writer;
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;
    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;
    private Long hit;
    private String genre;

    public BoardRegistDto(Board board) {
        this.bno = board.getBno();
        this.writer = board.getWriter();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.hit = board.getHit();
        this.genre = board.getGenre();
    }
}

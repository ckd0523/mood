package com.codehows.board.dto;

import com.codehows.board.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddReplyDto {
    private Long rno;
    private Long bno;
    private String uid;
    private String reply;
    private Long parentRno;
    private List<AddReplyDto> childReplies;

    public AddReplyDto(Reply reply) {
        this.rno = reply.getRno();
        this.bno = reply.getBoard().getBno();
        this.uid = reply.getUser().getUid();
        this.reply = reply.getReply();
        this.parentRno = reply.getParent() != null ? reply.getParent().getRno() : null;
        this.childReplies = new ArrayList<>();
    }
}

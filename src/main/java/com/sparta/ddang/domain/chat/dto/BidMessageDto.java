package com.sparta.ddang.domain.chat.dto;

import com.sparta.ddang.domain.chat.entity.BidMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BidMessageDto {


    public enum MessageType {
        ENTER, TALK
    }

    private BidMessage.MessageType type;
    //채팅방 ID
    private String roomId;
    //보내는 사람
    private String sender;
    //내용
    private String message;

    private LocalDateTime createdAt;


}

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
    private String roomId;
    private String sender;
    private String message;
    private LocalDateTime createdAt;

}

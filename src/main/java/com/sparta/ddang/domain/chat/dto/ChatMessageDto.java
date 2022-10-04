package com.sparta.ddang.domain.chat.dto;


import com.sparta.ddang.domain.chat.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageDto {

    public enum MessageType {
        ENTER, TALK
    }

    private ChatMessage.MessageType type;
    private String roomId;
    private String sender;
    private String message;
    private String profileImgUrl;
    private LocalDateTime createdAt;

    public ChatMessageDto() {}

    @Builder
    public ChatMessageDto(ChatMessage.MessageType type,
                          String roomId, String sender,
                          String message, LocalDateTime createdAt,
                          String profileImgUrl) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.createdAt = createdAt;
        this.profileImgUrl = profileImgUrl;
    }

    public ChatMessageDto(ChatMessage chatMessage, LocalDateTime localDateTime) {
        this.roomId = chatMessage.getRoomId();
        this.sender = chatMessage.getSender();
        this.message = chatMessage.getMessage();
        this.createdAt = localDateTime;
    }

}

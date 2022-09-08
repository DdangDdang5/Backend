package com.sparta.ddang.domain.chat.dto;

import com.sparta.ddang.domain.chat.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto {
    private String roomId;
    private ChatMessage.MessageType type;
    private String sender;
    private String message;

    @Builder
    public ChatMessageDto(String roomId, ChatMessage.MessageType type, String sender, String message) {
        this.roomId = roomId;
        this.type = type;
        this.sender = sender;
        this.message = message;
    }

    public ChatMessageDto() {}
}

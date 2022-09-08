package com.sparta.ddang.domain.chat.dto;

import com.sparta.ddang.domain.chat.entity.ChatMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequestDto {

    private ChatMessage.MessageType type;
    private Long roomId;
    private Long memberId;
    private String sender;
    private String message;
    private String createdAt;
}

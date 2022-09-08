package com.sparta.ddang.domain.chat.entity;

import com.sparta.ddang.domain.chat.dto.ChatMessageRequestDto;
import com.sparta.ddang.util.Timestamped;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class ChatMessage extends Timestamped {
    // 메시지 타입 : 입장, 채팅, 나가기
    public enum MessageType {
        ENTER, TALK, QUIT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long roomId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String message;

    @Builder
    public ChatMessage(Long roomId, MessageType type, String sender, String message) {
        this.roomId = roomId;
        this.type = type;
        this.sender = sender;
        this.message = message;
    }

    @Builder
    public ChatMessage(ChatMessageRequestDto chatMessageRequestDto) {
        this.type = chatMessageRequestDto.getType();
        this.roomId = chatMessageRequestDto.getRoomId();
        this.memberId = chatMessageRequestDto.getMemberId();
        this.sender = chatMessageRequestDto.getSender();
        this.message = chatMessageRequestDto.getMessage();
    }

    public ChatMessage() {}
}

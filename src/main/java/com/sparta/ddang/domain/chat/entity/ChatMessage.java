package com.sparta.ddang.domain.chat.entity;

import com.sparta.ddang.util.Timestamped;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
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
    private String roomId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String message;

    @Builder
    public ChatMessage(String roomId, MessageType type, String sender, String message) {
        this.roomId = roomId;
        this.type = type;
        this.sender = sender;
        this.message = message;
    }

    public ChatMessage() {}
}

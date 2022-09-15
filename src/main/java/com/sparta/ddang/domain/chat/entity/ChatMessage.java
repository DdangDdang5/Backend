package com.sparta.ddang.domain.chat.entity;


import com.sparta.ddang.domain.chat.dto.ChatMessageDto;
import com.sparta.ddang.util.TimestampedChat;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class ChatMessage extends TimestampedChat {

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

    @Column(nullable = false)
    private String nickName;

    @Column
    private String profileImgUrl;

    public ChatMessage(){}

    @Builder
    public ChatMessage(ChatMessageDto chatMessageDto, String nickName, String profileImgUrl){

        this.type = chatMessageDto.getType();
        this.roomId = chatMessageDto.getRoomId();
        this.sender = chatMessageDto.getSender();
        this.message = chatMessageDto.getMessage();
        this.nickName = nickName;
        this.profileImgUrl = profileImgUrl;

    }

}

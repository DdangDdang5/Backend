package com.sparta.ddang.domain.chat.entity;


import com.sparta.ddang.domain.chat.dto.ChatMessageDto;
import com.sparta.ddang.util.TimestampedChat;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Entity
public class ChatMessage extends TimestampedChat implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    public enum MessageType {
        ENTER, TALK, QUIT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String roomName;

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

    @Column
    private String createdAtString;


    public ChatMessage(){}

    @Builder
    public ChatMessage(ChatMessageDto chatMessageDto, String nickName, String profileImgUrl, String createdAtString){

        this.type = chatMessageDto.getType();
        this.roomId = chatMessageDto.getRoomId();
        this.sender = chatMessageDto.getSender();
        this.message = chatMessageDto.getMessage();
        this.nickName = nickName;
        this.profileImgUrl = profileImgUrl;
        this.createdAtString = createdAtString;

    }

    public ChatMessage(ChatMessage chatMessage){

        this.type = chatMessage.getType();
        this.roomId = chatMessage.getRoomId();
        this.sender = chatMessage.getSender();
        this.message = chatMessage.getMessage();
        this.nickName = chatMessage.getNickName();
        this.profileImgUrl = chatMessage.getProfileImgUrl();






    }

    public void addChatRoomName(String roomName){

        this.roomName = roomName;


    }

}

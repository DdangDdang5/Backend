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
    //채팅방 ID
    private String roomId;
    //보내는 사람
    private String sender;
    //내용
    private String message;

    // private String nickName;

    // private String profileImgUrl;

    private LocalDateTime createdAt;

    public ChatMessageDto(){

    }


    @Builder
    public ChatMessageDto(ChatMessage.MessageType type,
                          String roomId,String sender,
                          String message,LocalDateTime createdAt){

        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.createdAt =createdAt;


    }

//    public void addMember(String nickName, String profileImgUrl){
//
//        this.nickName = nickName;
//        this.profileImgUrl = profileImgUrl;
//
//    }

}

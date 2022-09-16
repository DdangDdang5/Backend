package com.sparta.ddang.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OnoChatMessageDto {

    private String roomId;

    private String roomName;

    private String profileImg;

    private String message;

    private LocalDateTime createdAt;


    @Builder
    public OnoChatMessageDto(String roomId,String roomName,
                             String profileImg,LocalDateTime createdAt,
                             String message){

        this.roomId = roomId;
        this.roomName = roomName;
        this.profileImg = profileImg;
        this.createdAt = createdAt;
        this.message = message;


    }


}
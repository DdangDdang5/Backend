package com.sparta.ddang.domain.chat.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatRoomResponseDto {
    private String roomId;
    private String roomName;
    private LocalDateTime createdAt;

    @Builder
    public ChatRoomResponseDto(String roomId,String roomName,
                               LocalDateTime createdAt){
        this.roomId = roomId;
        this.roomName = roomName;
        this.createdAt = createdAt;
    }

}

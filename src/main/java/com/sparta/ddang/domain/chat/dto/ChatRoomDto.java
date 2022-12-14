package com.sparta.ddang.domain.chat.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomDto {
    private String roomId;
    private String roomName;

    public static ChatRoomDto create(String name) {
        ChatRoomDto room = new ChatRoomDto();
        room.roomId = UUID.randomUUID().toString();
        room.roomName = name;
        return room;
    }

    @Builder
    public ChatRoomDto(String roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

}

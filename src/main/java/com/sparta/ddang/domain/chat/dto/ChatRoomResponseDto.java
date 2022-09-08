package com.sparta.ddang.domain.chat.dto;

import com.sparta.ddang.domain.chat.entity.ChatRoom;
import com.sparta.ddang.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomResponseDto {
    private Long id;
    private String channel;
    private Member member;


    public ChatRoomResponseDto(ChatRoom chatRoom, Member writer) {
        this.id = chatRoom.getId();
        this.channel = chatRoom.getRoomName();
        this.member = writer;
    }
}

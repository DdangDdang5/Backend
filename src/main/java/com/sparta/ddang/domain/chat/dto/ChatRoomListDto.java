package com.sparta.ddang.domain.chat.dto;

import com.sparta.ddang.domain.chat.entity.ChatRoom;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.util.Timestamped;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatRoomListDto extends Timestamped {

    private Long id;
    private String channel;
    private List<Member> memberList;
    private String nickname;

    public ChatRoomListDto(ChatRoom chatRoom, Member member) {
        this.id = chatRoom.getId();
        this.channel = chatRoom.getRoomName();
        this.memberList = chatRoom.getMemberList();
        this.nickname = member.getNickName();
    }

}

package com.sparta.ddang.domain.chat.entity;

import com.sparta.ddang.domain.chat.dto.ChatRoomDto;
import com.sparta.ddang.util.Timestamped;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Entity
public class ChatRoom extends Timestamped implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String roomId;

    @Column
    private String roomName;

    public ChatRoom(){}

    @Builder
    public ChatRoom(ChatRoomDto chatRoomDto){
        this.roomId = chatRoomDto.getRoomId();
        this.roomName = chatRoomDto.getRoomName();
    }

}

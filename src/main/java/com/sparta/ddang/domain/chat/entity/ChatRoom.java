package com.sparta.ddang.domain.chat.entity;

import com.sparta.ddang.domain.auction.entity.Auction;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String nickName;

    public static ChatRoom create(Auction auction, String nickName) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = String.valueOf(auction.getId());
        chatRoom.nickName = nickName;
        return chatRoom;
    }
}

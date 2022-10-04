package com.sparta.ddang.domain.chat.entity;

import com.sparta.ddang.util.TimestampedChat;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
public class OnoChatMessage extends TimestampedChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false)
    private String lastSpeaker;

    @Column(nullable = false)
    private String seller;

    @Column(nullable = false)
    private String bidder;

    @Column(nullable = false)
    private String message;

    @Column
    private String profileImgUrl;

    @Column
    private LocalDateTime lastMessageTime;

    @Column
    private Long auctionId;

    public OnoChatMessage(){}

    public OnoChatMessage(String roomId, String roomName,String lastSpeaker,
                          String message,String profileImgUrl,Long auctionId,
                          String seller,String bidder,LocalDateTime lastMessageTime){
        this.roomId = roomId;
        this.roomName = roomName;
        this.lastSpeaker = lastSpeaker;
        this.message = message;
        this.profileImgUrl = profileImgUrl;
        this.auctionId = auctionId;
        this.seller = seller;
        this.bidder = bidder;
        this.lastMessageTime = lastMessageTime;
    }

}

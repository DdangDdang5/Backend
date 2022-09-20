package com.sparta.ddang.domain.chat.entity;

import com.sparta.ddang.util.TimestampedChat;
import lombok.Getter;

import javax.persistence.*;

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
    private String nickName;

    @Column(nullable = false)
    private String message;

    @Column
    private String profileImgUrl;

    //@JoinColumn(name = "auction_id")
//    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
//    private Auction auction;
    @Column
    private Long auctionId;

    public OnoChatMessage(){}
    public OnoChatMessage(String roomId, String roomName,String nickName,
                          String message,String profileImgUrl,Long auctionId){

        this.roomId = roomId;
        this.roomName = roomName;
        this.nickName = nickName;
        this.message = message;
        this.profileImgUrl = profileImgUrl;
        this.auctionId = auctionId;


    }

    public void updateMessage(){

    }


}

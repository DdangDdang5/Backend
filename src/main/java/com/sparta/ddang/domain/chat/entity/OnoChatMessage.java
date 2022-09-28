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
    private String lastSpeaker;

    @Column(nullable = false)
    private String seller;

    @Column(nullable = false)
    private String bidder;

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
    public OnoChatMessage(String roomId, String roomName,String lastSpeaker,
                          String message,String profileImgUrl,Long auctionId,
                          String seller,String bidder){

        this.roomId = roomId;
        this.roomName = roomName;
        this.lastSpeaker = lastSpeaker;
        this.message = message;
        this.profileImgUrl = profileImgUrl;
        this.auctionId = auctionId;
        this.seller = seller;
        this.bidder = bidder;


    }

    public void updateMessage(){

    }


}

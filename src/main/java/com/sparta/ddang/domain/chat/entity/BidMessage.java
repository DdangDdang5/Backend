package com.sparta.ddang.domain.chat.entity;

import com.sparta.ddang.domain.chat.dto.BidMessageDto;
import com.sparta.ddang.util.TimestampedChat;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class BidMessage extends TimestampedChat {


    public enum MessageType {
        ENTER, TALK, QUIT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String nickName;

    public BidMessage(){}

    @Builder
    public BidMessage(BidMessageDto bidMessageDto,String nickName){

        this.type = bidMessageDto.getType();
        this.roomId = bidMessageDto.getRoomId();
        this.sender = bidMessageDto.getSender();
        this.message = bidMessageDto.getMessage();
        this.nickName = nickName;


    }


}

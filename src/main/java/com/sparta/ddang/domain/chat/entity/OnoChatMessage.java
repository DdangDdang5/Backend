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

    public OnoChatMessage(){}
    public OnoChatMessage(String roomId, String roomName,String nickName,
                          String message,String profileImgUrl){

        this.roomId = roomId;
        this.roomName = roomName;
        this.nickName = nickName;
        this.message = message;
        this.profileImgUrl = profileImgUrl;


    }

    public void updateMessage(){
        
    }




}

package com.sparta.ddang.domain.chat.dto;

import com.sparta.ddang.domain.mulltiimg.entity.MultiImage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OnoChatMessageDto {

    private String roomId;

    private String roomName;

    private String profileImg;

    private String message;

    private LocalDateTime lastMessageTime;

    private Long auctionId;

    private String auctionTitle;

    private List<MultiImage> multiImages;


    @Builder
    public OnoChatMessageDto(String roomId,String roomName,
                             String profileImg,LocalDateTime lastMessageTime,
                             String message, Long auctionId,String auctionTitle,
                             List<MultiImage> multiImages){

        this.roomId = roomId;
        this.roomName = roomName;
        this.profileImg = profileImg;
        this.message = message;
        this.auctionId = auctionId;
        this.auctionTitle = auctionTitle;
        this.multiImages = multiImages;
        this.lastMessageTime = lastMessageTime;

    }


}
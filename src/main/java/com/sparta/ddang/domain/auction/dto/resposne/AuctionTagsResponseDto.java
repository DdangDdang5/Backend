package com.sparta.ddang.domain.auction.dto.resposne;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.ddang.domain.mulltiimg.entity.MultiImage;
import com.sparta.ddang.domain.tag.entity.Tags;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder // 이건 빌더를 써야될것 같아서 사용함.
public class AuctionTagsResponseDto {


    //”auctionId”: 1,
    private Long auctionId;

    private String productName;

    private Tags tags;

    //”memberId”: 1,
    private Long memberId;
    //”nickname”: “nickname”,
    private String nickname;
    //”profileImgUrl”: “http://img.com”,
    private String profileImgUrl;
    //”title”: “title”,
    private String title;
    //”content”: “content”,
    private String content;
    //”auctionImgUrl”:”http://auctionimg.com”,
    private List<MultiImage> multiImages;
    //”startPrice”: 5000,
    private Long startPrice;
    //”nowPrice”: 5000,
    private Long nowPrice;
    //”auctionPeriod”: 3,
    private Long auctionPeriod;
    //”category”: “가구”,
    private String category;
    //”region”: “용산구”,
    private String region;
    //”direct”: true,
    private boolean direct;
    //”delivery”: true,
    private boolean delivery;
    //”viewerCnt”: 0,
    private Long viewerCnt;
    //”participantCnt”: 0,
    private Long participantCnt;
    //”auctionStatus”: true,
    private boolean auctionStatus;
    //”participantStatus”: false,
    private boolean participantStatus;

    //”favoriteStatus”: false,
    //private boolean favoriteStatus;

    //”createdAt”: “2022-08-27 15:30:00”,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    //”modifiedAt”: “2022-08-27 15:30:00”
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime modifiedAt;


}

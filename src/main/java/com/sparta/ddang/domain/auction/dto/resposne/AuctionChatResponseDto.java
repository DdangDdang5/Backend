package com.sparta.ddang.domain.auction.dto.resposne;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.ddang.domain.mulltiimg.entity.MultiImage;
import com.sparta.ddang.domain.tag.entity.Tags;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class AuctionChatResponseDto {
    private Long auctionId;
    private String productName;
    private Tags tags;
    private Long memberId;
    private String nickname;
    private String profileImgUrl;
    private String title;
    private String content;
    private List<MultiImage> multiImages;
    private Long startPrice;
    private Long nowPrice;
    private Long auctionPeriod;
    private String category;
    private String region;
    private boolean direct;
    private boolean delivery;
    private Long viewerCnt;
    private Long participantCnt;
    private boolean auctionStatus;
    private boolean participantStatus;
    private String roomId;
    private String bidId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;
}

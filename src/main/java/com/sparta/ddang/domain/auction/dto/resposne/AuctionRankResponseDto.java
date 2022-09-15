package com.sparta.ddang.domain.auction.dto.resposne;

import com.sparta.ddang.domain.mulltiimg.entity.MultiImage;
import com.sparta.ddang.domain.tag.entity.Tags;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AuctionRankResponseDto {

    private Long auctionId;

    private Long memberId;

    private String title;
    //”content”: “content”,
    private String content;
    //”auctionImgUrl”:”http://auctionimg.com”,
    private Long nowPrice;

    private Long viewerCnt;

    private List<MultiImage> multiImages;

    private Tags tags;



}

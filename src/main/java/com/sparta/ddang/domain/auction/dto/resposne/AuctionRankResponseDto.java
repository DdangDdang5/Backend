package com.sparta.ddang.domain.auction.dto.resposne;

import com.sparta.ddang.domain.mulltiimg.entity.MultiImage;
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

    private String content;

    private String category;

    private Long nowPrice;

    private Long viewerCnt;

    private boolean delivery;

    private boolean direct;

    private List<MultiImage> multiImages;



}

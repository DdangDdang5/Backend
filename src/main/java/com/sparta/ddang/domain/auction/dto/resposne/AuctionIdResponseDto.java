package com.sparta.ddang.domain.auction.dto.resposne;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AuctionIdResponseDto {
    private Long auctionId;

    @Builder
    public AuctionIdResponseDto(Long auctionId){
        this.auctionId = auctionId;
    }
}

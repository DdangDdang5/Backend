package com.sparta.ddang.domain.auction.dto.resposne;

import lombok.Getter;

@Getter
public class DoneAuctionResponseDto {
    private Long auctionId;
    private boolean auctionDone;

    public DoneAuctionResponseDto() {}

    public DoneAuctionResponseDto(Long auctionId, boolean auctionDone) {
        this.auctionId = auctionId;
        this.auctionDone = auctionDone;
    }
}

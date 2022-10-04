package com.sparta.ddang.domain.auction.dto.resposne;

import lombok.Getter;

@Getter
public class DoneAuctionResponseDto {
    private Long auctionId;
    private boolean sellerDone;
    private boolean bidderDone;
    private boolean isSeller;

    public DoneAuctionResponseDto() {}

    public DoneAuctionResponseDto(Long auctionId, boolean sellerDone, boolean bidderDone, boolean isSeller) {
        this.auctionId = auctionId;
        this.sellerDone = sellerDone;
        this.bidderDone = bidderDone;
        this.isSeller = isSeller;
    }
}

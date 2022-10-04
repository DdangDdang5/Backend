package com.sparta.ddang.domain.auction.dto.resposne;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BidderResponseDto {
    private Long auctionId;
    private String seller;
    private String bidder;
    private String roomId;

    @Builder
    public BidderResponseDto(Long auctionId, String seller, String bidder, String roomId) {
        this.auctionId = auctionId;
        this.seller = seller;
        this.bidder = bidder;
        this.roomId = roomId;
    }

    public BidderResponseDto() {}
}

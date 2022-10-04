package com.sparta.ddang.domain.auction.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuctionRequestDto {
    private String productName;
    private String title;
    private String content;
    private Long startPrice;
    private Long auctionPeriod;
    private String category;
    private String region;
    private boolean direct;
    private boolean delivery;
}

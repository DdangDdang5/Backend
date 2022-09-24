package com.sparta.ddang.domain.auction.dto.request;

import lombok.Getter;

@Getter
public class ReviewRequestDto {

    int trustPoint;

    public ReviewRequestDto(int trustPoint) {
        this.trustPoint = trustPoint;
    }

    public ReviewRequestDto() {}
}

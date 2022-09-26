package com.sparta.ddang.domain.favorite.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class FavoriteResponseDto {

    private Long auctionId;
    private Long memberId;
    private String nickname;
    private boolean favoriteStatus;


    @Builder
    public FavoriteResponseDto(Long auctionId, Long memberId, String nickname, boolean favoriteStatus){

        this.auctionId = auctionId;
        this.memberId = memberId;
        this.nickname = nickname;
        this.favoriteStatus = favoriteStatus;


    }


}

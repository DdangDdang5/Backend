package com.sparta.ddang.domain.favorite.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class FavoriteResponseDto {

    private Long autionId;
    private Long memberId;
    private String nickname;
    private boolean favoriteStatus;


    @Builder
    public FavoriteResponseDto(Long autionId, Long memberId, String nickname, boolean favoriteStatus){

        this.autionId = autionId;
        this.memberId = memberId;
        this.nickname = nickname;
        this.favoriteStatus = favoriteStatus;


    }


}

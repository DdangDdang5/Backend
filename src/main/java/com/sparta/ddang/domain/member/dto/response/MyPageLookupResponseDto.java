package com.sparta.ddang.domain.member.dto.response;

import com.sparta.ddang.domain.auction.dto.resposne.AuctionResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyPageLookupResponseDto {
    private Long memberId;
    private String email;
    private String nickname;
    private String profileImgUrl;
    private String trustGrade;
    private List<AuctionResponseDto> auctionResponseDtoList;

    public MyPageLookupResponseDto(){}

    @Builder
    public MyPageLookupResponseDto(Long memberId, String email,
                                   String nickname, String profileImgUrl, String trustGrade,
                                   List<AuctionResponseDto> auctionResponseDtoList){
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.profileImgUrl= profileImgUrl;
        this.trustGrade = trustGrade;
        this.auctionResponseDtoList = auctionResponseDtoList;
    }

}

package com.sparta.ddang.domain.member.dto.response;


import lombok.Builder;
import lombok.Getter;

@Getter
public class GetMypageResponseDto {

    private Long memberId;
    private String email;
    private String nickname;
    private String profileImgUrl;
    private Long myAuctionCnt;
    private Long myParticipantCnt;
    private Long myFavoriteCnt;
    private String trustGrade;

    public GetMypageResponseDto(){}

    @Builder
    public GetMypageResponseDto(Long memberId, String email,
                                String nickname, String profileImgUrl,
                                Long myAuctionCnt,Long myParticipantCnt,
                                Long myFavoriteCnt, String trustGrade) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.myAuctionCnt = myAuctionCnt;
        this.myParticipantCnt = myParticipantCnt;
        this.myFavoriteCnt = myFavoriteCnt;
        this.trustGrade = trustGrade;
    }
}

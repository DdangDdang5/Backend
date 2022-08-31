package com.sparta.ddang.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MypageResponseDto {
    private Long memberId;
    private String email;
    private String nickname;
    private String profileImgUrl;
    private String phoneNum;

    @Builder
    public MypageResponseDto(Long memberId, String email, String nickname, String profileImgUrl, String phoneNum) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.phoneNum = phoneNum;
    }

    public MypageResponseDto() {}
}
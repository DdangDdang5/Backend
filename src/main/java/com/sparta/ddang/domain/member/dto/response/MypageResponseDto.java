package com.sparta.ddang.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MypageResponseDto {
    private Long memberId;
    private String email;
    private String nickname;
    private String profileImgUrl;

    @Builder
    public MypageResponseDto(Long memberId, String email, String nickname, String profileImgUrl) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
    }

    public MypageResponseDto() {}
}

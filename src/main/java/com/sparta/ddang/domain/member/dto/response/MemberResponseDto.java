package com.sparta.ddang.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponseDto {
    private Long memberId;
    private String email;
    private String nickName;
    private boolean isKakao;

    @Builder
    public MemberResponseDto(Long memberId, String email, String nickName ,boolean isKakao) {
        this.memberId = memberId;
        this.email = email;
        this.nickName = nickName;
        this.isKakao = isKakao;
    }

    public MemberResponseDto() {}
}

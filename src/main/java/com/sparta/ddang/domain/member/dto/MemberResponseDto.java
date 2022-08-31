package com.sparta.ddang.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponseDto {
    private Long memberId;
    private String email;
    private String nickName;

    @Builder
    public MemberResponseDto(Long memberId, String email, String nickName) {
        this.memberId = memberId;
        this.email = email;
        this.nickName = nickName;
    }

    public MemberResponseDto() {}
}

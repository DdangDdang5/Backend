package com.sparta.ddang.domain.member.dto.response;

import com.sparta.ddang.jwt.TokenDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class KakaoLoginResponseDto {

    private Long memberId;
    private String email;
    private String nickname;
    private String kakaoProImg;
    private boolean isKakao;
    private TokenDto tokenDto;

    public KakaoLoginResponseDto(){

    }

    @Builder
    public KakaoLoginResponseDto(Long memberId, String email, String nickname,
                                 String kakaoProImg, boolean isKakao,
                                 TokenDto tokenDto){
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.kakaoProImg = kakaoProImg;
        this.isKakao = isKakao;
        this.tokenDto = tokenDto;


    }


}



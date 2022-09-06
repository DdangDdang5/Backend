package com.sparta.ddang.domain.member.dto.response;

import com.sparta.ddang.jwt.TokenDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class KakaoLoginResponseDto {


    //private Long id;
    private String email;
    private String nickname;
    private String kakaoProImg;
    private boolean isKakao;
    private TokenDto tokenDto;

    public KakaoLoginResponseDto(){

    }

    @Builder
    public KakaoLoginResponseDto(String email, String nickname,
                                 String kakaoProImg, boolean isKakao,
                                 TokenDto tokenDto){

        this.email = email;
        this.nickname = nickname;
        this.kakaoProImg = kakaoProImg;
        this.isKakao = isKakao;
        this.tokenDto = tokenDto;


    }


}



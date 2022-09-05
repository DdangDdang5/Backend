package com.sparta.ddang.domain.member.dto.response;


import lombok.Builder;
import lombok.Getter;



@Getter
public class KakaoUserInfoDto {

    //private Long id;
    private String email;
    private String nickname;
    private String kakaoProImg;
    private boolean isKakao;


    public KakaoUserInfoDto(){

    }

    @Builder
    public KakaoUserInfoDto(String nickname,String email,String kakaoProImg,boolean isKakao){
        this.nickname = nickname;
        this.email = email;
        this.kakaoProImg = kakaoProImg;
        this.isKakao = isKakao;

    }




}

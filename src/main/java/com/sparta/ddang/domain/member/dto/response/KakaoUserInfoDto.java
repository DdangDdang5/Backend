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
    public KakaoUserInfoDto(String email,String nickname,String kakaoProImg,boolean isKakao){

    }




}

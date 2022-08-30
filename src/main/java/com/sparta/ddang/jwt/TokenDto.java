package com.sparta.ddang.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenDto {
    private String grantType;
    private String accessToken;
    //  private String refreshToken;
    private Long accessTokenExpiresIn;

    @Builder
    public TokenDto(String grantType, String accessToken, Long accessTokenExpiresIn) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }

    public TokenDto() {}
}

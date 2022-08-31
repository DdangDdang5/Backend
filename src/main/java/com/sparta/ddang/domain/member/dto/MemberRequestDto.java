package com.sparta.ddang.domain.member.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class MemberRequestDto {

    @NotBlank
    private String email;

    @NotBlank
    private String nickName;

    @NotBlank
    private String password;

    @NotBlank
    private String phoneNum;

    public MemberRequestDto(String email, String nickName, String password, String phoneNum) {
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.phoneNum = phoneNum;
    }

    public MemberRequestDto() {}

}

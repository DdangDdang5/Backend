package com.sparta.ddang.domain.member.dto.response;

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

//    @NotBlank
//    private String phoneNum;

    public MemberRequestDto(String email, String nickName, String password) {
        this.email = email;
        this.nickName = nickName;
        this.password = password;

    }

    public MemberRequestDto() {}

}

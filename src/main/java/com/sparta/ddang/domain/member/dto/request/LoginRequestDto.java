package com.sparta.ddang.domain.member.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class LoginRequestDto {
    @NotBlank
    private String email;

    @NotBlank
    private String password;

}
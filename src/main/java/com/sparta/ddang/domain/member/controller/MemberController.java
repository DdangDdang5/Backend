package com.sparta.ddang.domain.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.ddang.domain.dto.ResponseDto;
import com.sparta.ddang.domain.member.dto.request.EmailRequestDto;
import com.sparta.ddang.domain.member.dto.request.LoginRequestDto;
import com.sparta.ddang.domain.member.dto.request.NicknameRequestDto;
import com.sparta.ddang.domain.member.dto.response.MemberRequestDto;
import com.sparta.ddang.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;


    @GetMapping("/ci")
    public ResponseDto<?> successCI() {
        String successStr = "자동배포 성공!!!";
        return ResponseDto.success(successStr);
    }
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseDto<?> signup(@RequestBody MemberRequestDto requestDto) throws IOException {
        return memberService.createMember(requestDto);
    }

    @RequestMapping(value = "/emailcheck", method = RequestMethod.POST)
    public ResponseDto<?> emailCheck(@RequestBody EmailRequestDto emailRequestDto) {
        return memberService.emailCheck(emailRequestDto);
    }

    @RequestMapping(value = "/nicknamecheck", method = RequestMethod.POST)
    public ResponseDto<?> nickNameCheck(@RequestBody NicknameRequestDto nicknameRequestDto) {
        return memberService.nickNameCheck(nicknameRequestDto);
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseDto<?> login(@RequestBody LoginRequestDto requestDto,
                                HttpServletResponse response) {
        return memberService.login(requestDto, response);
    }

    @RequestMapping(value = "/kakao/callback", method = RequestMethod.GET)
    public ResponseDto<?> kakaoLogin(@RequestParam String code,
                                     HttpServletResponse response) throws JsonProcessingException {
        return memberService.kakaoLogin(code,response);
    }

    @RequestMapping(value = "/{memberId}/mypage", method = RequestMethod.GET)
    public ResponseDto<?> getMypage(@PathVariable Long memberId, HttpServletRequest request) {
        return memberService.getMypage(memberId, request);
    }

    @RequestMapping(value = "/{memberId}/mypage", method = RequestMethod.PATCH)
    public ResponseDto<?> editMypage(@PathVariable Long memberId, @RequestPart("data")MemberRequestDto requestDto, @RequestPart("profileImg") MultipartFile multipartFile) throws IOException {
        return memberService.editMypage(memberId, requestDto, multipartFile);
    }

    @RequestMapping(value = "/{memberId}/lookup", method = RequestMethod.GET)
    public ResponseDto<?> lookUpNickName(@PathVariable Long memberId) {
        return memberService.lookUpmemberId(memberId);
    }

    @RequestMapping(value = "/{memberId}/trust-point", method = RequestMethod.GET)
    public ResponseDto<?> getTrustPoint(@PathVariable Long memberId) {
        return memberService.getTrustPoint(memberId);
    }

}

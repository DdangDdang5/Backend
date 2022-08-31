package com.sparta.ddang.domain.member.controller;

import com.sparta.ddang.domain.dto.ResponseDto;
import com.sparta.ddang.domain.member.dto.LoginRequestDto;
import com.sparta.ddang.domain.member.dto.MemberRequestDto;
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

    @PostMapping("/signup")
    public ResponseDto<?> signup(@RequestBody MemberRequestDto requestDto) throws IOException {
        return memberService.createMember(requestDto);
    }

    @PostMapping("/login")
    public ResponseDto<?> login(@RequestBody LoginRequestDto requestDto,
                                HttpServletResponse response) {
        return memberService.login(requestDto, response);
    }

    @GetMapping("/{memberId}/mypage")
    public ResponseDto<?> getMypage(@PathVariable Long memberId, HttpServletRequest request) {
        return memberService.getMypage(memberId, request);
    }

    @PatchMapping("/{memberId}/mypage")
    public ResponseDto<?> editMypage(@PathVariable Long memberId, @RequestPart("data")MemberRequestDto requestDto, @RequestPart("profileImg") MultipartFile multipartFile) throws IOException {
        return memberService.editMypage(memberId, requestDto, multipartFile);
    }

}

package com.sparta.ddang.domain.member.service;

import com.sparta.ddang.domain.dto.ResponseDto;
import com.sparta.ddang.domain.member.dto.LoginRequestDto;
import com.sparta.ddang.domain.member.dto.MemberRequestDto;
import com.sparta.ddang.domain.member.dto.MemberResponseDto;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.domain.member.repository.MemberRepository;
import com.sparta.ddang.jwt.TokenDto;
import com.sparta.ddang.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public ResponseDto<?> createMember(MemberRequestDto requestDto) throws IOException {

        if (null != checkEmail(requestDto.getEmail())) {
            return ResponseDto.fail("이미 존재하는 아이디입니다.");
        }

        Member member = Member.builder()
                .email(requestDto.getEmail())
                .nickName(requestDto.getNickName())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .phoneNum(requestDto.getPhoneNum())
                .build();

        memberRepository.save(member);

        return ResponseDto.success(
                MemberResponseDto.builder()
                        .memberId(member.getId())
                        .email(member.getEmail())
                        .nickName(member.getNickName())
                        .build()
        );


    }


    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
        Member member = checkEmail(requestDto.getEmail());
        if (null == member) {
            return ResponseDto.fail("존재하지 않는 아이디입니다.");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        tokenToHeaders(tokenDto, response);


        return ResponseDto.success(
                MemberResponseDto.builder()
                        .memberId(member.getId())
                        .email(member.getEmail())
                        .nickName(member.getNickName())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public Member checkEmail(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        return optionalMember.orElse(null);
    }

    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
//    response.addHeader("Refresh-Token", "Bearer " + tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }

}


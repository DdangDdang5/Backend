package com.sparta.ddang.domain.member.service;

import com.sparta.ddang.domain.dto.ResponseDto;
import com.sparta.ddang.domain.member.dto.MemberRequestDto;
import com.sparta.ddang.domain.member.dto.MemberResponseDto;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseDto<?> createMember(MemberRequestDto requestDto) throws IOException {

        if (null != isPresentMemberByEmail(requestDto.getEmail())) {
            return ResponseDto.fail("이미 존재하는 아이디입니다.");
        }

        if (null != isPresentMemberByNickName(requestDto.getNickName())) {
            return ResponseDto.fail("이미 존재하는 닉네임입니다.");
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
    @Transactional(readOnly = true)
    public Member isPresentMemberByEmail(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        return optionalMember.orElse(null);
    }
    @Transactional(readOnly = true)
    public Member isPresentMemberByNickName(String nickName) {
        Optional<Member> optionalMember = memberRepository.findByNickName(nickName);
        return optionalMember.orElse(null);
    }


}

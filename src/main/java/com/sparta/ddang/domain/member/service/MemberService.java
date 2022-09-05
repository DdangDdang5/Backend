package com.sparta.ddang.domain.member.service;

import com.sparta.ddang.domain.dto.ResponseDto;
import com.sparta.ddang.domain.member.dto.request.EmailRequestDto;
import com.sparta.ddang.domain.member.dto.request.LoginRequestDto;
import com.sparta.ddang.domain.member.dto.request.NicknameRequestDto;
import com.sparta.ddang.domain.member.dto.response.MemberRequestDto;
import com.sparta.ddang.domain.member.dto.response.MemberResponseDto;
import com.sparta.ddang.domain.member.dto.response.MypageResponseDto;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.domain.member.repository.MemberRepository;
import com.sparta.ddang.jwt.TokenDto;
import com.sparta.ddang.jwt.TokenProvider;
import com.sparta.ddang.util.S3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final S3UploadService s3UploadService;

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
                //.phoneNum(requestDto.getPhoneNum())
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


    // 이메일 중복확인.
    @Transactional
    public ResponseDto<?> emailCheck(EmailRequestDto email) {

        String emailCheck = email.getEmail();

        if (emailCheck.equals("")) {

            return ResponseDto.success("이메일을 입력해주세요.");

        }
        if (!emailCheck.contains("@")) {
            return ResponseDto.success("이메일 형식이 아닙니다.");
        }

        if (null != checkEmail(emailCheck)) { // 이메일 중복이면
            return ResponseDto.success(false);
        } else { // 이메일 중복 아니면
            return ResponseDto.success(true);
        }
    }


    // 닉네임 중복확인.
    @Transactional
    public ResponseDto<?> nickNameCheck(NicknameRequestDto nickname) {

        String nickNameCheck = nickname.getNickName();

        if (nickNameCheck.equals("")) {
            log.info("빈값이다.");
            return ResponseDto.success("닉네임을 입력해주세요");

        } else {
            log.info("빈값이 아니다.");
            if (null != checkNickname(nickNameCheck)) { // 넥네임 중복이면
                return ResponseDto.success(false);
            } else { // 닉네임 중복 아니면
                return ResponseDto.success(true);
            }

        }


    }


    @Transactional
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
    @Transactional
    public ResponseDto<?> getMypage(Long memberId, HttpServletRequest request) {

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        Member member = checkMemberId(memberId);

        if (null == member) {
            return ResponseDto.fail("존재하지 않는 아이디입니다.");
        }

        return ResponseDto.success(
                MypageResponseDto.builder()
                        .memberId(member.getId())
                        .email(member.getEmail())
                        .nickname(member.getNickName())
                        .profileImgUrl(member.getProfileImgUrl())
                        //.phoneNum(member.getPhoneNum())
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> editMypage(Long memberId, MemberRequestDto requestDto, MultipartFile multipartFile) throws IOException {
        Member member = checkMemberId(memberId);

        if (member == null) {
            return ResponseDto.fail("존재하지 않는 회원입니다.");
        }

        String profileImgUrl = member.getProfileImgUrl();
        if (!multipartFile.isEmpty()) {
            profileImgUrl = s3UploadService.upload(multipartFile, "DdangDdang/profileImg");
        } else {
            profileImgUrl = null;
        }

        member.update(requestDto.getNickName(),profileImgUrl);

        return ResponseDto.success(
                MypageResponseDto.builder()
                        .memberId(member.getId())
                        .email(member.getEmail())
                        .nickname(member.getNickName())
                        .profileImgUrl(member.getProfileImgUrl())
                        //.phoneNum(member.getPhoneNum())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public Member checkEmail(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        return optionalMember.orElse(null);
    }

    @Transactional(readOnly = true)
    public Member checkNickname(String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickName(nickname);
        return optionalMember.orElse(null);
    }

    @Transactional(readOnly = true)
    public Member checkMemberId(Long id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        return optionalMember.orElse(null);
    }

    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
//    response.addHeader("Refresh-Token", "Bearer " + tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }


}


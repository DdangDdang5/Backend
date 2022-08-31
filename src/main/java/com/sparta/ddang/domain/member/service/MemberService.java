package com.sparta.ddang.domain.member.service;

import com.sparta.ddang.domain.dto.ResponseDto;
import com.sparta.ddang.domain.member.dto.LoginRequestDto;
import com.sparta.ddang.domain.member.dto.MemberRequestDto;
import com.sparta.ddang.domain.member.dto.MemberResponseDto;
import com.sparta.ddang.domain.member.dto.MypageResponseDto;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.domain.member.repository.MemberRepository;
import com.sparta.ddang.jwt.TokenDto;
import com.sparta.ddang.jwt.TokenProvider;
import com.sparta.ddang.util.S3UploadService;
import lombok.RequiredArgsConstructor;
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
                        .phoneNum(member.getPhoneNum())
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

        member.update(requestDto.getNickName(),requestDto.getPhoneNum(),profileImgUrl);

        return ResponseDto.success(
                MypageResponseDto.builder()
                        .memberId(member.getId())
                        .email(member.getEmail())
                        .nickname(member.getNickName())
                        .profileImgUrl(member.getProfileImgUrl())
                        .phoneNum(member.getPhoneNum())
                        .build()
        );
    }
//    @Transactional
//    public ResponseDto<?> deleteMember(Long memberId, HttpServletRequest request) {
//
//        if (null == request.getHeader("Authorization")) {
//            return ResponseDto.fail("로그인이 필요합니다.");
//        }
//
//        Member member = validateMember();
//        if (null == member) {
//            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
//        }
//
//        Post post = isPresentPost(postId);
//        if (null == post) {
//            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
//        }
//
//        if (post.validateMember(member)) {
//            return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
//        }
//
//        postRepository.delete(post);
//
//        return ResponseDto.success(null);
//    }



    @Transactional(readOnly = true)
    public Member checkEmail(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
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


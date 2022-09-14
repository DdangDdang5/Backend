package com.sparta.ddang.domain.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.ddang.domain.auction.dto.resposne.AuctionResponseDto;
import com.sparta.ddang.domain.auction.entity.Auction;
import com.sparta.ddang.domain.auction.repository.AuctionRepository;
import com.sparta.ddang.domain.dto.ResponseDto;
import com.sparta.ddang.domain.favorite.repository.FavoriteRespository;
import com.sparta.ddang.domain.member.dto.request.EmailRequestDto;
import com.sparta.ddang.domain.member.dto.request.LoginRequestDto;
import com.sparta.ddang.domain.member.dto.request.NicknameRequestDto;
import com.sparta.ddang.domain.member.dto.response.*;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.domain.member.entity.MemberDetails;
import com.sparta.ddang.domain.member.repository.MemberRepository;
import com.sparta.ddang.domain.participant.repository.ParticipantRepository;
import com.sparta.ddang.jwt.TokenDto;
import com.sparta.ddang.jwt.TokenProvider;
import com.sparta.ddang.util.S3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final S3UploadService s3UploadService;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final AuctionRepository auctionRepository;

    private final ParticipantRepository participantRepository;

    private final FavoriteRespository favoriteRespository;

    @Value("${kakao.appkey}")
    private String kakaoAppKey;

    @Transactional
    public ResponseDto<?> createMember(MemberRequestDto requestDto) throws IOException {

        if (null != checkEmail(requestDto.getEmail())) {
            return ResponseDto.fail("이미 존재하는 이메일입니다.");
        }

        Member member = Member.builder()
                .email(requestDto.getEmail())
                .nickName(requestDto.getNickName())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .isKakao(false)
                .build();

        memberRepository.save(member);

        return ResponseDto.success(
                MemberResponseDto.builder()
                        .memberId(member.getId())
                        .email(member.getEmail())
                        .nickName(member.getNickName())
                        .isKakao(member.isKakao())
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
            return ResponseDto.fail("존재하지 않는 이메일입니다.");
        }

        if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
            return ResponseDto.fail("비밀번호를 다시 확인해 주세요");
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        tokenToHeaders(tokenDto, response);

        return ResponseDto.success(
                MemberResponseDto.builder()
                        .memberId(member.getId())
                        .email(member.getEmail())
                        .nickName(member.getNickName())
                        .isKakao(member.isKakao())
                        .build()
        );

    }


    @Transactional
    public ResponseDto<?> kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {

        String accessToken = getAccessToken(code);

        KakaoUserInfoDto kakaoUserInfoDto = getKakaoUserInfo(accessToken);

        Member member = registerKakaoUserIfNeeded(kakaoUserInfoDto);

        // 4. 강제 로그인 처리
        forceLogin(member);

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        tokenToHeaders(tokenDto, response);

        return ResponseDto.success(
                KakaoLoginResponseDto.builder()
                        .memberId(member.getId())
                        .email(member.getEmail())
                        .nickname(member.getNickName())
                        .kakaoProImg(member.getProfileImgUrl())
                        .isKakao(member.isKakao())
                        .tokenDto(tokenDto)
                        .build()
        );


    }


    @Transactional
    public String getAccessToken(String code) throws JsonProcessingException {

        // 1. "인가 코드"로 "액세스 토큰" 요청
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoAppKey);
        //body.add("redirect_uri", "http://localhost:8080/member/kakao/callback");
        //body.add("redirect_uri", "http://localhost:3000/member/kakao/callback");
        body.add("redirect_uri", "https://sysgood.shop/member/kakao/callback");
        body.add("code", code);
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String accessToken = jsonNode.get("access_token").asText();

        return accessToken;
    }

    @Transactional
    public KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {

        // 2. 토큰으로 카카오 API 호출
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        System.out.println(jsonNode);

        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();

        String profileImg = jsonNode.get("properties").get("profile_image").asText();

        System.out.println("카카오 사용자 정보: " + id + ", " + nickname + ", " + email + "," + profileImg);

        KakaoUserInfoDto kakaoUserInfoDto = new KakaoUserInfoDto(nickname, email, profileImg, true);

        return kakaoUserInfoDto;

    }

    @Transactional
    public Member registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfoDto) {

        String femail = kakaoUserInfoDto.getEmail();
        Member kakaoMember = memberRepository.findByEmail(femail)
                .orElse(null);

//        Member kakaoMember = memberRepository.findByEmail(femail).orElseThrow(
//                () -> new IllegalArgumentException("이미 가입한 이메일입니다.")
//        );

        // 회원가입
        // username: kakao nickname
        if (kakaoMember == null) {

            String nickname = kakaoUserInfoDto.getNickname() + "kakao" + UUID.randomUUID().toString();

            String password = UUID.randomUUID().toString();

            System.out.println("aaa" + UUID.randomUUID());

            String encodedpassword = passwordEncoder.encode(password);

            String email = kakaoUserInfoDto.getEmail();

            String profileImg = kakaoUserInfoDto.getKakaoProImg();

            //UserRoleEnum role = UserRoleEnum.USER;

            kakaoMember = new Member(email, nickname, encodedpassword, profileImg, true);

            memberRepository.save(kakaoMember);

        }

        return kakaoMember;
    }

    @Transactional
    public void forceLogin(Member kakaoMember) {
        UserDetails userDetails = new MemberDetails(kakaoMember);
        Authentication authentication
                = new UsernamePasswordAuthenticationToken(userDetails,
                null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
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

        Long myAction = auctionRepository.countAllByMemberId(memberId);
        Long myParticipant = participantRepository.countAllByMemberId(memberId);
        Long myFavorite = favoriteRespository.countAllByMemberId(memberId);


        return ResponseDto.success(
                GetMypageResponseDto.builder()
                        .memberId(member.getId())
                        .email(member.getEmail())
                        .nickname(member.getNickName())
                        .profileImgUrl(member.getProfileImgUrl())
                        .myAuctionCnt(myAction)
                        .myParticipantCnt(myParticipant)
                        .myFavoriteCnt(myFavorite)
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> editMypage(Long memberId, MemberRequestDto requestDto, MultipartFile multipartFile) throws IOException {
        Member member = checkMemberId(memberId);
        
        if (member == null) {
            return ResponseDto.fail("존재하지 않는 회원입니다.");
        }       
        
        Member member1 = checkNickname(requestDto.getNickName());

        if (member1 != null) {
            return ResponseDto.fail("닉네임이 존재합니다.");
        }      

        String profileImgUrl = member.getProfileImgUrl();

        if (!multipartFile.isEmpty()) {
            profileImgUrl = s3UploadService.upload(multipartFile, "DdangDdang/profileImg");
        }

        member.update(requestDto.getNickName(), profileImgUrl);

        return ResponseDto.success(
                MypageResponseDto.builder()
                        .memberId(member.getId())
                        .email(member.getEmail())
                        .nickname(member.getNickName())
                        .profileImgUrl(member.getProfileImgUrl())
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> lookUpmemberId(Long memberId) {

        Member member = checkOthermemberId(memberId);

        if (member == null){
            return ResponseDto.fail("존재하지 않는 회원입니다.");
        }

        List<Auction> auctionList = auctionRepository.findAllByMember_Id(member.getId());

        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        for (Auction auction : auctionList){

            auctionResponseDtoList.add(

                    AuctionResponseDto.builder()
                            .auctionId(auction.getId())
                            .productName(auction.getProductName())
                            .memberId(auction.getMember().getId())
                            .nickname(auction.getMember().getNickName())
                            .profileImgUrl(auction.getMember().getProfileImgUrl())
                            .title(auction.getTitle())
                            .content(auction.getContent())
                            .multiImages(auction.getMultiImages())
                            .startPrice(auction.getStartPrice())
                            .nowPrice(auction.getNowPrice())
                            .auctionPeriod(auction.getAuctionPeriod())
                            .category(auction.getCategory())
                            .region(auction.getRegion())
                            .direct(auction.isDirect())
                            .delivery(auction.isDelivery())
                            .viewerCnt(auction.getViewerCnt())
                            .auctionStatus(true)
                            .participantCnt(auction.getParticipantCnt())
                            .participantStatus(auction.isParticipantStatus())
                            //.favoriteStatus(auction.isFavoriteStatus())
                            .createdAt(auction.getCreatedAt())
                            .modifiedAt(auction.getModifiedAt())
                            .build()
            );


        }

        return ResponseDto.success(
                MyPageLookupResponseDto.builder()
                        .memberId(member.getId())
                        .email(member.getEmail())
                        .nickname(member.getNickName())
                        .profileImgUrl(member.getProfileImgUrl())
                        .auctionResponseDtoList(auctionResponseDtoList)
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
    public Member checkOthermemberId(Long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        return optionalMember.orElse(null);
    }

    @Transactional(readOnly = true)
    public Member checkMemberId(Long id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        return optionalMember.orElse(null);
    }

    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }




}


package com.sparta.ddang.domain.auction.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.ddang.domain.auction.dto.request.AuctionRequestDto;
import com.sparta.ddang.domain.auction.dto.request.AuctionTagsRequestDto;
import com.sparta.ddang.domain.auction.dto.request.AuctionUpdateRequestDto;
import com.sparta.ddang.domain.auction.dto.request.JoinPriceRequestDto;
import com.sparta.ddang.domain.auction.dto.resposne.*;
import com.sparta.ddang.domain.auction.entity.Auction;
import com.sparta.ddang.domain.auction.repository.AuctionRepository;
import com.sparta.ddang.domain.category.dto.CategoryOnlyResponseDto;
import com.sparta.ddang.domain.category.dto.CategoryResponseDto;
import com.sparta.ddang.domain.category.entity.Category;
import com.sparta.ddang.domain.category.repository.CategoryRepository;
import com.sparta.ddang.domain.chat.dto.ChatRoomDto;
import com.sparta.ddang.domain.chat.service.ChatService;
import com.sparta.ddang.domain.dto.ResponseDto;
import com.sparta.ddang.domain.favorite.dto.FavoriteResponseDto;
import com.sparta.ddang.domain.favorite.entity.Favorite;
import com.sparta.ddang.domain.favorite.repository.FavoriteRespository;
import com.sparta.ddang.domain.joinprice.entity.JoinPrice;
import com.sparta.ddang.domain.joinprice.repository.JoinPriceRepository;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.domain.member.repository.MemberRepository;
import com.sparta.ddang.domain.mulltiimg.awsS3exceptionhandler.FileTypeErrorException;
import com.sparta.ddang.domain.mulltiimg.entity.MultiImage;
import com.sparta.ddang.domain.mulltiimg.repository.MultiImgRepository;
import com.sparta.ddang.domain.participant.entity.Participant;
import com.sparta.ddang.domain.participant.repository.ParticipantRepository;
import com.sparta.ddang.domain.region.dto.RegionOnlyResponseDto;
import com.sparta.ddang.domain.region.dto.RegionResponseDto;
import com.sparta.ddang.domain.region.entity.Region;
import com.sparta.ddang.domain.region.repository.RegionRepository;
import com.sparta.ddang.domain.tag.entity.Tags;
import com.sparta.ddang.domain.tag.repository.TagsRepository;
import com.sparta.ddang.domain.viewcnt.entity.ViewCnt;
import com.sparta.ddang.domain.viewcnt.repository.ViewCntRepository;
import com.sparta.ddang.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Slf4j
@Service
public class AuctionService {

    private final AmazonS3Client amazonS3Client;

    private final MultiImgRepository multiImgRepository;

    private final AuctionRepository auctionRepository;

    private final TokenProvider tokenProvider;

    private final ViewCntRepository viewCntRepository;

    private final ParticipantRepository participantRepository;

    private final CategoryRepository categoryRepository;

    private final RegionRepository regionRepository;

    private final FavoriteRespository favoriteRespository;

    private final TagsRepository tagsRepository;

    private final JoinPriceRepository joinPriceRepository;

    private final ChatService chatService;

    private final MemberRepository memberRepository;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷 이름

    @Transactional
    public ResponseDto<?> getAllAuction() {


        List<Auction> auctionList = auctionRepository.findAllByOrderByModifiedAtDesc();

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
        //return ResponseDto.success(auctionRepository.findAllByOrderByModifiedAtDesc());
        return ResponseDto.success(auctionResponseDtoList);

    }
    // 토큰값 없어도 됨.
    // 시큐리티에서 상세페이지의 권한을 풀어줌
    // 일반 사용자는 조회수에 영향을 미치면 안됨
    // 회원들만 조회수에 영향을 미침
    // 회원들도 게시글당 1번만 영향을 미침.
    @Transactional
    public ResponseDto<?> getDetailAuction(Long auctionId, HttpServletRequest request) {

        Auction auction = checkAuction(auctionId);

        if (auction == null) {

            return ResponseDto.fail("해당 경매 게시글이 없습니다.");

        }

        // 회원일경우 토큰값을 받아오니까 회원정보를 가져옴
        Member member = validateMember(request);

        // 일반인들에게는 토큰이 없음 대신 해당 상세페이지만 보여줌.
        if (null == member) {

            return ResponseDto.success(auction);

        }

        // 만약 댓글사용시 댓글 로직 추가.

        // 회원일경우 멤버 고유 번호를 가져옴
        Long memId = member.getId();

        // 해당 경매 게시글 번호를 가져옴
        Long aucId = auctionId;

        // 만약 해당 게시글에 방문한적이 있으면 그냥 해당 게시글만 보여줌
        if (viewCntRepository.existsByMemberIdAndAuctionId(memId, aucId)) {

            return ResponseDto.success(auction);

        }

        // 처음 방문하면 ViewCnt테이블에 회원 정보와 해당 게시글 정보를 저장함.
        ViewCnt viewCnt = new ViewCnt(memId, aucId);

        viewCntRepository.save(viewCnt);

        // 해당 게시글의 카운트 수를 증가시킴 --> 해당 게시글을 처음 방문한 회원만 조회수를 증가시킴.
        auction.cntAuction();

        // 적용된 변경사항을 저장함.
        auctionRepository.save(auction);

        // 카테고리 viewer 추가
        String cate = auction.getCategory();
        Long cateCnt = auction.getViewerCnt();

        Category category= checkCategory(cate);


        if (categoryRepository.existsByCategory(cate)) {

            category.updateCateCnt(cate);

            categoryRepository.save(category);

        }

        if (category == null) {

            category = new Category(cate, cateCnt);

            categoryRepository.save(category);
            
            // 카테고리 전체 합산하기

        }

        //Category category = new Category(cate, cateCnt);

        // 지역 viewer 추가

        String regi = auction.getRegion();
        Long regionCnt = auction.getViewerCnt();

        Region region= checkRegion(regi);

        if (regionRepository.existsByRegion(regi)) {

            region.updateRegionCnt(regi);

            regionRepository.save(region);

        }

        if (region == null) {

            region = new Region(regi, regionCnt);

            regionRepository.save(region);

        }

//        ChatMessageDto chatMessageDto = new ChatMessageDto();
//        chatMessageDto.addMember(member.getNickName(), member.getProfileImgUrl());
//        chatService.save(chatMessageDto);

        return ResponseDto.success(auction);


    }
    
    @Transactional
    public ResponseDto<?> createAuction(List<MultipartFile> multipartFile,
                                        AuctionRequestDto auctionRequestDto,
                                        AuctionTagsRequestDto auctionTagsRequestDto,
                                        HttpServletRequest request) throws IOException {


        System.out.println("==================================================");
        System.out.println(auctionRequestDto.getTitle());
        System.out.println(auctionRequestDto.getProductName());
        System.out.println(auctionRequestDto.getContent());

        System.out.println(auctionRequestDto.getStartPrice());
        System.out.println(auctionRequestDto.getAuctionPeriod());

        System.out.println(auctionRequestDto.getCategory());
        System.out.println(auctionRequestDto.getRegion());

        System.out.println(auctionRequestDto.isDelivery());
        System.out.println(auctionRequestDto.isDirect());
        System.out.println("==================================================");


        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }

        //List<Auction> auctionList = auctionRepository.findAll();

        List<MultiImage> multiImages = new ArrayList<>();

        Auction auction = new Auction(multiImages, member, auctionRequestDto);

        Tags tags = new Tags(member.getId(), auction.getId(), auctionTagsRequestDto);

        auction.addAuctionTags(tags);

        tagsRepository.save(tags);

        auctionRepository.save(auction);

        tags.addAuctionId(auction.getId());

        tagsRepository.save(tags);

        auctionRepository.save(auction);

        List<String> imgUrlList = new ArrayList<>();

        for (MultipartFile imgFile : multipartFile) {
            String fileName = upload(imgFile);
            String imgUrl = URLDecoder.decode(fileName, "UTF-8");
            if (imgUrl.equals("false")) {
                return ResponseDto.fail("이미지 파일만 업로드 가능합니다.");
            }
            // 이미지 저장 및 이미지테이블이랑 포스트테이블매핑하기

            MultiImage multiImage = new MultiImage(imgUrl, auction.getId(), member.getId());

            multiImages.add(multiImage);

            imgUrlList.add(imgUrl);

            multiImgRepository.save(multiImage);

            System.out.println("========================");
            System.out.println(multiImages.get(0));

            System.out.println("========================");


            // 컬럼이 0이면 처음일것이다. --> 기본데이터
            if (categoryRepository.count() == 0){

                Category cat0 = new Category("전체품목",0L);
                categoryRepository.save(cat0);
                Category cat1 = new Category("가구인테리어",0L);
                categoryRepository.save(cat1);
                Category cat2 = new Category("가전",0L);
                categoryRepository.save(cat2);
                Category cat3 = new Category("남성패션",0L);
                categoryRepository.save(cat3);
                Category cat4 = new Category("여성패션",0L);
                categoryRepository.save(cat4);
                Category cat5 = new Category("악세서리",0L);
                categoryRepository.save(cat5);
                Category cat6 = new Category("스포츠레저",0L);
                categoryRepository.save(cat6);
                Category cat7 = new Category("취미게임악기",0L);
                categoryRepository.save(cat7);
                Category cat8 = new Category("디지털",0L);
                categoryRepository.save(cat8);
                Category cat9 = new Category("뷰티미용",0L);
                categoryRepository.save(cat9);



            }

            if (regionRepository.count() == 0){

                Region reg0 = new Region("서울전체",0L);
                regionRepository.save(reg0);
                Region reg1 = new Region("강남구",0L);
                regionRepository.save(reg1);
                Region reg2 = new Region("강동구",0L);
                regionRepository.save(reg2);
                Region reg3 = new Region("강북구",0L);
                regionRepository.save(reg3);
                Region reg4 = new Region("강서구",0L);
                regionRepository.save(reg4);
                Region reg5 = new Region("관악구",0L);
                regionRepository.save(reg5);
                Region reg6 = new Region("광진구",0L);
                regionRepository.save(reg6);
                Region reg7 = new Region("구로구",0L);
                regionRepository.save(reg7);
                Region reg8 = new Region("금천구",0L);
                regionRepository.save(reg8);
                Region reg9 = new Region("노원구",0L);
                regionRepository.save(reg9);
                Region reg10 = new Region("도봉구",0L);
                regionRepository.save(reg10);
                Region reg11 = new Region("동대문구",0L);
                regionRepository.save(reg11);
                Region reg12 = new Region("동작구",0L);
                regionRepository.save(reg12);
                Region reg13 = new Region("마포구",0L);
                regionRepository.save(reg13);
                Region reg14 = new Region("서대문구",0L);
                regionRepository.save(reg14);
                Region reg15 = new Region("서초구",0L);
                regionRepository.save(reg15);
                Region reg16 = new Region("성동구",0L);
                regionRepository.save(reg16);
                Region reg17 = new Region("성북구",0L);
                regionRepository.save(reg17);
                Region reg18 = new Region("송파구",0L);
                regionRepository.save(reg18);
                Region reg19 = new Region("양천구",0L);
                regionRepository.save(reg19);
                Region reg20 = new Region("영등포구",0L);
                regionRepository.save(reg20);
                Region reg21 = new Region("용산구",0L);
                regionRepository.save(reg21);
                Region reg22 = new Region("은평구",0L);
                regionRepository.save(reg22);
                Region reg23 = new Region("종로구",0L);
                regionRepository.save(reg23);
                Region reg24 = new Region("성동구",0L);
                regionRepository.save(reg24);
                Region reg25 = new Region("중구",0L);
                regionRepository.save(reg25);
                Region reg26 = new Region("중랑구",0L);
                regionRepository.save(reg26);


            }

        }

        // 경매 게시글 생성시 동시에 채팅방 개설
        String auchatName = "경매" + auction.getId() + "번방";

        ChatRoomDto chatRoomDto = chatService.createRoom(auchatName);

        //채팅방 아이디 필요하면 resposne하기
        System.out.println("경매채팅방 아이디 : " + chatRoomDto.getRoomId());

        auction.addAuctionChatRoomId(chatRoomDto.getRoomId());


        // 경매 게시글 생성시 동시에 채팅방 개설
        String aucBidName = "경매 호가" + auction.getId() + "번방";

        ChatRoomDto chatRoomDto1 = chatService.createRoom(aucBidName);

        //채팅방 아이디 필요하면 resposne하기
        System.out.println("경매 호가방 아이디 : " + chatRoomDto1.getRoomId());

        auction.addAuctionBidRoomId(chatRoomDto1.getRoomId());

        auctionRepository.save(auction);

        // 상세페이지에서 채팅 roomId 반환하기




        //auction.getCategory()
        //auction.getViewerCnt()

        //auction = new Auction(multiImages);

//        return ResponseDto.success(
//                AuctionIdResponseDto.builder()
//                        .auctionId(auction.getId())
//                        .build()
//        );



        return ResponseDto.success(
                AuctionChatResponseDto.builder()
                        .auctionId(auction.getId())
                        .productName(auction.getProductName())
                        .tags(auction.getTags())
                        .memberId(member.getId())
                        .nickname(member.getNickName())
                        .profileImgUrl(member.getProfileImgUrl())
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
                        .auctionStatus(auction.isAuctionStatus())
                        .participantCnt(auction.getParticipantCnt())
                        .participantStatus(auction.isParticipantStatus())
                        //.favoriteStatus(auction.isFavoriteStatus())
                        .roomId(auction.getChatRoomId())
                        .bidId(auction.getBidRoomId())
                        .createdAt(auction.getCreatedAt())
                        .modifiedAt(auction.getModifiedAt())
                        .build()
        );


    }

    @Transactional
    public ResponseDto<?> updateAuction(List<MultipartFile> multipartFile,
                                        Long auctionId,
                                        AuctionUpdateRequestDto auctionUpdateRequestDto,
                                        AuctionTagsRequestDto auctionTagsRequestDto,
                                        HttpServletRequest request) throws IOException {

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }

        Auction auction = checkAuction(auctionId);

        if (auction == null) {

            return ResponseDto.fail("해당 경매 게시글이 없습니다.");

        }

        Tags tags = checkTags(auctionId);

        tags.updateTags(auctionTagsRequestDto);

        tagsRepository.save(tags);


        //수정시 해당 경매게시글에 있는 이미지 전체 삭제
        multiImgRepository.deleteAllByMemberIdAndAuctionId(member.getId(), auctionId);

        List<MultiImage> multiImages = new ArrayList<>();

        auction.updateAuction(multiImages, member, auctionUpdateRequestDto);

        auction.addAuctionTags(tags);

        auctionRepository.save(auction);

        List<String> imgUrlList = new ArrayList<>();

        for (MultipartFile imgFile : multipartFile) {
            String fileName = upload(imgFile);
            String imgUrl = URLDecoder.decode(fileName, "UTF-8");
            if (imgUrl.equals("false")) {
                return ResponseDto.fail("이미지 파일만 업로드 가능합니다.");
            }
            // 이미지 저장 및 이미지테이블이랑 포스트테이블매핑하기

            MultiImage multiImage = new MultiImage(imgUrl, auction.getId(), member.getId());

            multiImages.add(multiImage);

            imgUrlList.add(imgUrl);

            multiImgRepository.save(multiImage);

        }


        return ResponseDto.success(
                AuctionTagsResponseDto.builder()
                        .auctionId(auction.getId())
                        .productName(auction.getProductName())
                        .tags(auction.getTags())
                        .memberId(member.getId())
                        .nickname(member.getNickName())
                        .profileImgUrl(member.getProfileImgUrl())
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
                        .auctionStatus(auction.isAuctionStatus())
                        .participantCnt(auction.getParticipantCnt())
                        .participantStatus(auction.isParticipantStatus())
                        //.favoriteStatus(auction.isFavoriteStatus())
                        .createdAt(auction.getCreatedAt())
                        .modifiedAt(auction.getModifiedAt())
                        .build()
        );

    }

    @Transactional
    public ResponseDto<?> deleteAuction(Long auctionId,
                                        HttpServletRequest request) {

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }

        //수정시 해당 경매게시글에 있는 이미지 전체 삭제
        //multiImgRepository.deleteAllByMemberIdAndAuctionId(member.getId(), auctionId);

        auctionRepository.deleteById(auctionId);

        //tagsRepository.deleteByAuctionId(auctionId);

        return ResponseDto.successToMessage(200, "게시물이 성공적으로 삭제되었습니다", null);


    }

    @Transactional
    public ResponseDto<?> findCategoryAuction(String category) {

        List<Auction> auctionList = auctionRepository.findAllByCategory(category);

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
                            .auctionStatus(auction.isAuctionStatus())
                            .participantCnt(auction.getParticipantCnt())
                            .participantStatus(false) // 사용자에게 보여지는 부분
                            //.favoriteStatus(auction.isFavoriteStatus())
                            .createdAt(auction.getCreatedAt())
                            .modifiedAt(auction.getModifiedAt())
                            .build()
            );

        }


        // return ResponseDto.success(auctionRepository.findAllByCategory(category));
        return ResponseDto.success(auctionResponseDtoList);

    }

    @Transactional
    public ResponseDto<?> findRegionAuction(String region) {

        List<Auction> auctionList = auctionRepository.findAllByRegion(region);

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
                            .auctionStatus(auction.isAuctionStatus())
                            .participantCnt(auction.getParticipantCnt())
                            .participantStatus(false) // 사용자에게 보여지는 부분
                            //.favoriteStatus(auction.isFavoriteStatus())
                            .createdAt(auction.getCreatedAt())
                            .modifiedAt(auction.getModifiedAt())
                            .build()
            );

        }


        //return ResponseDto.success(auctionRepository.findAllByRegion(region));
        return ResponseDto.success(auctionResponseDtoList);

    }

    @Transactional
    public ResponseDto<?> findCategoryAndRegionAuction(String category, String region) {

        List<Auction> auctionList = auctionRepository.findAllByCategoryAndRegion(category, region);

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
                            .auctionStatus(auction.isAuctionStatus())
                            .participantCnt(auction.getParticipantCnt())
                            .participantStatus(false) // 사용자에게 보여지는 부분
                            //.favoriteStatus(auction.isFavoriteStatus())
                            .createdAt(auction.getCreatedAt())
                            .modifiedAt(auction.getModifiedAt())
                            .build()
            );

        }

        //return ResponseDto.success(auctionRepository.findAllByCategoryAndRegion(category, region));

        return ResponseDto.success(auctionResponseDtoList);

    }
    
    // 경매 참여하기 --> 입찰하기
    @Transactional
    public ResponseDto<?> joinAuction(Long auctionId, JoinPriceRequestDto joinPriceRequestDto,
                                      HttpServletRequest request) {

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }

        Auction auction = checkAuction(auctionId);

        if (auction == null) {

            return ResponseDto.fail("해당 경매 게시글이 없습니다.");

        }

        // 원래 참가하기 코드 --> 입찰하기
        
//        if (participantRepository.existsByMemberIdAndAuctionId(member.getId(), auctionId)) {
//
//            participantRepository.deleteByMemberIdAndAuctionId(member.getId(), auctionId);
//
//            Long participantCnt = participantRepository.countAllByAuctionId(auctionId);
//
//            if (participantCnt > 0) {
//
//                auction.updateParticipantStatusOn();
//
//            } else {
//
//                auction.updateParticipantStatusOff();
//
//            }
//
//            auction.updateParticipantCnt(participantCnt);
//
//            auctionRepository.save(auction);
//
//
//            // 아래 리턴문이랑 동일하게 고치기 false로 해서 --> 고침
//            return ResponseDto.success(
//                    AuctionResponseDto.builder()
//                            .auctionId(auction.getId())
//                            .productName(auction.getProductName())
//                            .memberId(auction.getMember().getId())
//                            .nickname(auction.getMember().getNickName())
//                            .profileImgUrl(auction.getMember().getProfileImgUrl())
//                            .title(auction.getTitle())
//                            .content(auction.getContent())
//                            .multiImages(auction.getMultiImages())
//                            .startPrice(auction.getStartPrice())
//                            .nowPrice(auction.getNowPrice())
//                            .auctionPeriod(auction.getAuctionPeriod())
//                            .category(auction.getCategory())
//                            .region(auction.getRegion())
//                            .direct(auction.isDirect())
//                            .delivery(auction.isDelivery())
//                            .viewerCnt(auction.getViewerCnt())
//                            .auctionStatus(auction.isAuctionStatus())
//                            .participantCnt(auction.getParticipantCnt())
//                            .participantStatus(false) // 사용자에게 보여지는 부분
//                            //.favoriteStatus(auction.isFavoriteStatus())
//                            .createdAt(auction.getCreatedAt())
//                            .modifiedAt(auction.getModifiedAt())
//                            .build()
//            );
//
//        }

        // 원래 참가하기 코드 --> 입찰하기

        if (participantRepository.existsByMemberIdAndAuctionId(member.getId(), auctionId)) {

            //participantRepository.deleteByMemberIdAndAuctionId(member.getId(), auctionId);

            //Long participantCnt = participantRepository.countAllByAuctionId(auctionId);

            //auction.updateParticipantCnt(participantCnt);

            Long userPrice = joinPriceRequestDto.getUserPrice();

            JoinPrice joinPrice = new JoinPrice(member.getId(), auctionId, userPrice);

            joinPriceRepository.save(joinPrice);

            auction.updateJoinPrice(userPrice);

            auctionRepository.save(auction);

            // 아래 리턴문이랑 동일하게 고치기 false로 해서 --> 고침
            return ResponseDto.success(
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
                            .auctionStatus(auction.isAuctionStatus())
                            .participantCnt(auction.getParticipantCnt())
                            .participantStatus(false) // 사용자에게 보여지는 부분
                            //.favoriteStatus(auction.isFavoriteStatus())
                            .createdAt(auction.getCreatedAt())
                            .modifiedAt(auction.getModifiedAt())
                            .build()
            );

        }

        // 객체로 저장 But 기본키 인덱스 번호로 저장함
        // 따라서 participant 컬럼명을 member_id, auction_id로 저장함.
        Participant participant = new Participant(member, auction);

        participantRepository.save(participant);

        Long participantCnt = participantRepository.countAllByAuctionId(auctionId);

        auction.updateParticipantCnt(participantCnt);

        Long userPrice = joinPriceRequestDto.getUserPrice();

        JoinPrice joinPrice = new JoinPrice(member.getId(), auctionId, userPrice);

        joinPriceRepository.save(joinPrice);

        auction.updateJoinPrice(userPrice);

        auctionRepository.save(auction);

        return ResponseDto.success(
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
                        .auctionStatus(auction.isAuctionStatus())
                        .participantCnt(auction.getParticipantCnt())
                        .participantStatus(true) // 사용자에게 보여지는 부분
                        //.favoriteStatus(auction.isFavoriteStatus())
                        .createdAt(auction.getCreatedAt())
                        .modifiedAt(auction.getModifiedAt())
                        .build()
        );


    }

    // 내가 참여한 경매 
    @Transactional
    public ResponseDto<?> getAlljoinAuction(HttpServletRequest request) {

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("Authorization이 없습니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }

        if (participantRepository.countAllByMemberId(member.getId()) == 0) {

            return ResponseDto.fail("참여한 경매 상품이 없습니다.");

        }

        List<Participant> participantList = participantRepository.findAllByMember_Id(member.getId());

        ArrayList<AuctionResponseDto> auctionArrayList = new ArrayList<>();

        for (Participant participant : participantList) {

            auctionArrayList.add(
                    AuctionResponseDto.builder()
                            .auctionId(participant.getAuction().getId())
                            .memberId(participant.getAuction().getMember().getId())
                            .nickname(participant.getAuction().getMember().getNickName())
                            .profileImgUrl(participant.getAuction().getMember().getProfileImgUrl())
                            .title(participant.getAuction().getTitle())
                            .content(participant.getAuction().getContent())
                            .multiImages(participant.getAuction().getMultiImages())
                            .startPrice(participant.getAuction().getStartPrice())
                            .nowPrice(participant.getAuction().getNowPrice())
                            .auctionPeriod(participant.getAuction().getAuctionPeriod())
                            .category(participant.getAuction().getCategory())
                            .region(participant.getAuction().getRegion())
                            .direct(participant.getAuction().isDirect())
                            .delivery(participant.getAuction().isDelivery())
                            .viewerCnt(participant.getAuction().getViewerCnt())
                            .participantCnt(participant.getAuction().getParticipantCnt())
                            .participantStatus(participant.getAuction().isParticipantStatus())
                            .auctionStatus(participant.getAuction().isAuctionStatus())
                            .createdAt(participant.getAuction().getCreatedAt())
                            .modifiedAt(participant.getAuction().getModifiedAt())
                            .build()
            );

        }

        return ResponseDto.success(auctionArrayList);


    }

    // 경매 찜하기
    @Transactional
    public ResponseDto<?> addfavoriteAuction(Long auctionId, HttpServletRequest request) {

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }

        Auction auction = checkAuction(auctionId);

        if (auction == null) {

            return ResponseDto.fail("해당 경매 게시글이 없습니다.");

        }

        if(favoriteRespository.existsByMemberIdAndAuctionId(member.getId(), auctionId)){

            favoriteRespository.deleteByMemberIdAndAuctionId(member.getId(), auctionId);

            return ResponseDto.success(
                    FavoriteResponseDto.builder()
                            .autionId(auction.getId())
                            .memberId(auction.getMember().getId())
                            .nickname(member.getNickName())
                            .favoriteStatus(false)
                            .build()
            );

        }

        Favorite favorite = new Favorite(member,auction);

        favoriteRespository.save(favorite);

        return ResponseDto.success(
                FavoriteResponseDto.builder()
                        .autionId(auction.getId())
                        .memberId(auction.getMember().getId())
                        .nickname(member.getNickName())
                        .favoriteStatus(true)
                        .build()
        );


    }

    // 내가 관심이있는 경매 목록
    @Transactional
    public ResponseDto<?> myfavoriteAuction(HttpServletRequest request) {

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("Authorization이 없습니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }

        List<Favorite> favorites = favoriteRespository.findAllByMember_Id(member.getId());

        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        for (Favorite favorite : favorites){

            auctionResponseDtoList.add(
                    AuctionResponseDto.builder()
                            .auctionId(favorite.getAuction().getId())
                            .memberId(favorite.getAuction().getMember().getId())
                            .nickname(favorite.getAuction().getMember().getNickName())
                            .profileImgUrl(favorite.getAuction().getMember().getProfileImgUrl())
                            .title(favorite.getAuction().getTitle())
                            .content(favorite.getAuction().getContent())
                            .multiImages(favorite.getAuction().getMultiImages())
                            .startPrice(favorite.getAuction().getStartPrice())
                            .nowPrice(favorite.getAuction().getNowPrice())
                            .auctionPeriod(favorite.getAuction().getAuctionPeriod())
                            .category(favorite.getAuction().getCategory())
                            .region(favorite.getAuction().getRegion())
                            .direct(favorite.getAuction().isDirect())
                            .delivery(favorite.getAuction().isDelivery())
                            .viewerCnt(favorite.getAuction().getViewerCnt())
                            .participantCnt(favorite.getAuction().getParticipantCnt())
                            .participantStatus(favorite.getAuction().isParticipantStatus())
                            .auctionStatus(favorite.getAuction().isAuctionStatus())
                            .createdAt(favorite.getAuction().getCreatedAt())
                            .modifiedAt(favorite.getAuction().getModifiedAt())
                            .build()

            );

        }

        return ResponseDto.success(auctionResponseDtoList);


    }
    
    

    //내가 시작한 경매
    @Transactional
    public ResponseDto<?> getMyAuction(HttpServletRequest request) {

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("Authorization이 없습니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }


        List<Auction> auctionList = auctionRepository.findAllByMember_Id(member.getId());

        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        for (Auction auction : auctionList){

            auctionResponseDtoList.add(

                    AuctionResponseDto.builder()
                            .auctionId(auction.getId())
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
                            .participantCnt(auction.getParticipantCnt())
                            .participantStatus(auction.isParticipantStatus())
                            .auctionStatus(auction.isAuctionStatus())
                            .createdAt(auction.getCreatedAt())
                            .modifiedAt(auction.getModifiedAt())
                            .build()

            );



        }


        // return ResponseDto.success(auctionRepository.findAllByMember_Id(member.getId()));
        return ResponseDto.success(auctionResponseDtoList);


    }


    // 카테고리별 인기순 조회
    @Transactional
    public ResponseDto<?> getAllHitCategory() {

        if (categoryRepository.count() == 0){

            return ResponseDto.fail("카테고리가 없습니다.");

        }

        List<Category> categoryList = categoryRepository.findAllByOrderByViewerCntDesc();

        List<CategoryResponseDto> categories = new ArrayList<>();

        for (Category category : categoryList){

            categories.add(
                    CategoryResponseDto.builder()
                            .categoryId(category.getId())
                            .categoryName(category.getCategory())
                            .viewerCnt(category.getViewerCnt())
                            .build()
            );

        }

        return ResponseDto.success(categories);


    }

    @Transactional
    public ResponseDto<?> showCategoryAuction() {

        List<Category> categories = categoryRepository.findAllByOrderByCategoryAsc();

        List<CategoryOnlyResponseDto> categoryOnlyResponseDtos = new ArrayList<>();

        for (Category category : categories){

            categoryOnlyResponseDtos.add(
                    CategoryOnlyResponseDto.builder()
                            .categoryName(category.getCategory())
                            .build()
            );
        }

        return ResponseDto.success(categoryOnlyResponseDtos);

    }

    // 지역별 인기순 조회
    @Transactional
    public ResponseDto<?> getAllHitRegion() {

        if (regionRepository.count() == 0){

            return ResponseDto.fail("지역이 없습니다.");

        }

        List<Region> regionList = regionRepository.findAllByOrderByViewerCntDesc();

        List<RegionResponseDto> regions  = new ArrayList<>();

        for (Region region : regionList){

            regions.add(
                    RegionResponseDto.builder()
                            .regionId(region.getId())
                            .regionName(region.getRegion())
                            .viewerCnt(region.getViewerCnt())
                            .build()
            );


        }

        return ResponseDto.success(regions);
        
    }


    @Transactional
    public ResponseDto<?> showRegionAuction() {

        List<Region> regions = regionRepository.findAllByOrderByRegionAsc();

        List<RegionOnlyResponseDto> regionOnlyResponseDtos = new ArrayList<>();

        for (Region region : regions){

            regionOnlyResponseDtos.add(

                    RegionOnlyResponseDto.builder()
                            .region(region.getRegion())
                            .build()

            );

        }

        return ResponseDto.success(regionOnlyResponseDtos);


    }

    // 경매 검색
    @Transactional
    public ResponseDto<?> getSearchTitle(String title) {

        List<Auction> auctionList = auctionRepository.findByTitleContaining(title);

        List<AuctionResponseDto> auctionResponseDtoList =  new ArrayList<>();

        for (Auction auction : auctionList){

            auctionResponseDtoList.add(
                    AuctionResponseDto.builder()
                            .auctionId(auction.getId())
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
                            .participantCnt(auction.getParticipantCnt())
                            .participantStatus(auction.isParticipantStatus())
                            .auctionStatus(auction.isAuctionStatus())
                            .createdAt(auction.getCreatedAt())
                            .modifiedAt(auction.getModifiedAt())
                            .build()
            );


        }


        return ResponseDto.success(auctionResponseDtoList);


    }

    // 낙찰자 조회 및 낙찰자와 판매자 채팅방 개설
    @Transactional
    public ResponseDto<?> getBidder(Long auctionId) {
        // 판매자(경매 생성자) 조회
        Auction auction = checkAuction(auctionId);
        Member seller = auction.getMember();
        System.out.println("seller: "+seller);

        // 낙찰자(제일 높은 호가를 부른 사람) 조회
        List<JoinPrice> joinPriceList = joinPriceRepository.findAllByAuctionIdOrderByJoinPriceDesc(auctionId);
        JoinPrice joinPrice = joinPriceList.get(0);
        Member bidder = checkMember(joinPrice.getMemberId());
        System.out.println("bidder: "+bidder.getNickName());
        
        // 채팅방 생성
        ChatRoomDto chatRoomDto = chatService.createRoom("경매"+auctionId+"방 1:1 채팅방");
        System.out.println("roomId: "+chatRoomDto.getRoomId());

        return ResponseDto.success(
                BidderResponseDto.builder()
                        .auctionId(auctionId)
                        .seller(seller.getNickName())
                        .bidder(bidder.getNickName())
                        .roomId(chatRoomDto.getRoomId())
                        .build()
        );
    }

    //경매 To4조회
    @Transactional
    public ResponseDto<?> getAuctionTop4() {
        
        // viewCnt 순으로 정렬된 배열
        List<Auction> auctionList = auctionRepository.findAllByOrderByViewerCntDesc();

        // viewCnt 순으로 정렬된 배열을 저장
        List<AuctionRankResponseDto> auctionRankResponseDtos = new ArrayList<>();

        // top4만 저장하는 dto
        List<AuctionRankResponseDto> auctionRankResponseDtoList = new ArrayList<>();
        
        // for문으로 viewCnt 순으로 정렬
        for (Auction auction : auctionList){

            auctionRankResponseDtos.add(
                    AuctionRankResponseDto.builder()
                            .auctionId(auction.getId())
                            .memberId(auction.getMember().getId())
                            .title(auction.getTitle())
                            .content(auction.getContent())
                            .region(auction.getRegion())
                            .auctionPeriod(auction.getAuctionPeriod())
                            .createdAt(auction.getCreatedAt())
                            .nowPrice(auction.getNowPrice())
                            .viewerCnt(auction.getViewerCnt())
                            .delivery(auction.isDelivery())
                            .direct(auction.isDirect())
                            .multiImages(auction.getMultiImages())
                            .build()

            );


        }


        if (auctionRankResponseDtos.size() == 0){

            return ResponseDto.fail("게시글이 없습니다. 게시글을 등록해주세요");

        }

        if (auctionRankResponseDtos.size() == 1){

            for (int i = 0; i < 1 ; i++) {

                auctionRankResponseDtoList.add(
                        auctionRankResponseDtos.get(i)
                );

            }

            return ResponseDto.success(auctionRankResponseDtoList);

        }

        if (auctionRankResponseDtos.size() == 2){

            for (int i = 0; i < 2 ; i++) {

                auctionRankResponseDtoList.add(
                        auctionRankResponseDtos.get(i)
                );

            }

            return ResponseDto.success(auctionRankResponseDtoList);

        }

        if (auctionRankResponseDtos.size() == 3){

            for (int i = 0; i < 3 ; i++) {

                auctionRankResponseDtoList.add(
                        auctionRankResponseDtos.get(i)
                );

            }

            return ResponseDto.success(auctionRankResponseDtoList);

        }


        
        // 인기순 4개만 따로 배열로 저장
        for (int i = 0; i < 4 ; i++) {

            auctionRankResponseDtoList.add(
                    auctionRankResponseDtos.get(i)
            );

        }

        // 저장된 4개만 저장된 리스트 출력
        return ResponseDto.success(auctionRankResponseDtoList);


    }


    //최신 경매 조회(3개)
    @Transactional
    public ResponseDto<?> getNewReleaseTop3() {

        // 경매게시글 최신순으로 정렬된 배열
        List<Auction> auctionList = auctionRepository.findAllByOrderByCreatedAtDesc();

        // 경매게시글 최신순으로 정렬된 배열 저장
        List<AuctionRankResponseDto> auctionRankResponseDtos = new ArrayList<>();

        // 최신 경매 게시글 3개만 저장하는 dto
        List<AuctionRankResponseDto> auctionRankResponseDtoList = new ArrayList<>();


        // for문으로 최신 순으로 정렬된 것을 dto에 넣는다.
        for (Auction auction : auctionList){

            auctionRankResponseDtos.add(
                    AuctionRankResponseDto.builder()
                            .auctionId(auction.getId())
                            .memberId(auction.getMember().getId())
                            .title(auction.getTitle())
                            .content(auction.getContent())
                            .region(auction.getRegion())
                            .auctionPeriod(auction.getAuctionPeriod())
                            .createdAt(auction.getCreatedAt())
                            .nowPrice(auction.getNowPrice())
                            .viewerCnt(auction.getViewerCnt())
                            .delivery(auction.isDelivery())
                            .direct(auction.isDirect())
                            .multiImages(auction.getMultiImages())
                            .build()

            );


        }

        if (auctionRankResponseDtos.size() == 0){

            return ResponseDto.fail("게시글이 없습니다. 게시글을 등록해주세요");

        }



        if (auctionRankResponseDtos.size() == 1){

            for (int i = 0; i < 1 ; i++) {

                auctionRankResponseDtoList.add(
                        auctionRankResponseDtos.get(i)
                );

            }

            return ResponseDto.success(auctionRankResponseDtoList);

        }


        if (auctionRankResponseDtos.size() == 2){

            for (int i = 0; i < 2 ; i++) {

                auctionRankResponseDtoList.add(
                        auctionRankResponseDtos.get(i)
                );

            }

            return ResponseDto.success(auctionRankResponseDtoList);

        }



        // 최신 3개만 따로 배열로 저장
        for (int i = 0; i < 3 ; i++) {

            auctionRankResponseDtoList.add(
                    auctionRankResponseDtos.get(i)
            );

        }


        // 저장된 3개만 저장된 리스트 출력
        return ResponseDto.success(auctionRankResponseDtoList);


    }  
    


//======================== 회원 정보 및 경매 정보 ========================

    @Transactional
    public Member validateMember(HttpServletRequest request) {

        return tokenProvider.getMemberFromAuthentication();
    }


    @Transactional(readOnly = true)
    public Auction checkAuction(Long id) {
        Optional<Auction> optionalAuction = auctionRepository.findById(id);
        return optionalAuction.orElse(null);
    }

    @Transactional(readOnly = true)
    public Member checkMember(Long id) {
        Optional<Member> optionalAuction = memberRepository.findById(id);
        return optionalAuction.orElse(null);
    }

    // category확인
    @Transactional(readOnly = true)
    public Category checkCategory(String cate) {
        Optional<Category> optionalCategory = categoryRepository.findByCategory(cate);
        return optionalCategory.orElse(null);
    }

    //region 확인
    @Transactional(readOnly = true)
    public Region checkRegion(String regi) {
        Optional<Region> optionalRegion = regionRepository.findByRegion(regi);
        return optionalRegion.orElse(null);
    }

    @Transactional(readOnly = true)
    public Tags checkTags(Long auctionId) {
        Optional<Tags> optionalTags = tagsRepository.findByAuctionId(auctionId);
        return optionalTags.orElse(null);
    }


    // ========================= 파일업로드 관련 메서드 ==========================

    // 로컬에 파일 업로드 하기
    private Optional<File> convert(MultipartFile file) throws IOException {

        String type = file.getContentType();
        long size = file.getSize();
        System.out.println("====================================" + type);
        System.out.println("====================================" + size);

        // 파일 타입 예외처리
        if (!type.startsWith("image")) {

            throw new FileTypeErrorException();

        }

        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());


        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }


    // 로컬에 저장된 파일을 S3로 파일업로드 세팅 및 S3로 업로드
    public String upload(File uploadFile) {
        // S3에 저장될 파일 이름
        String fileName = "DdangDdang/auctionImg/" + UUID.randomUUID() + uploadFile.getName();
        // s3로 업로드 및  업로드 파일의 url을 String으로 받음.
        String uploadImageUrl = putS3(uploadFile, fileName);
        log.info(uploadImageUrl);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    // 이미지 변환 예외처리
    public String upload(MultipartFile multipartFile) throws IOException {
        File uploadFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
                .orElseThrow(() -> new IllegalArgumentException("error: 파일 변환에 실패했습니다"));

        return upload(uploadFile);
    }

    // S3로 업로드 하는 메서드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // s3에 파일 업로드 성공시 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }


}

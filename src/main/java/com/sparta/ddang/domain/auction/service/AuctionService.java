package com.sparta.ddang.domain.auction.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.ddang.domain.auction.dto.request.*;
import com.sparta.ddang.domain.auction.dto.resposne.*;
import com.sparta.ddang.domain.auction.entity.Auction;
import com.sparta.ddang.domain.auction.repository.AuctionRepository;
import com.sparta.ddang.domain.category.dto.CategoryOnlyResponseDto;
import com.sparta.ddang.domain.category.dto.CategoryResponseDto;
import com.sparta.ddang.domain.category.entity.Category;
import com.sparta.ddang.domain.category.repository.CategoryRepository;
import com.sparta.ddang.domain.chat.dto.ChatRoomDto;
import com.sparta.ddang.domain.chat.entity.ChatMessage;
import com.sparta.ddang.domain.chat.repository.ChatMessageJpaRepository;
import com.sparta.ddang.domain.chat.service.ChatRoomService;
import com.sparta.ddang.domain.chat.service.ChatService;
import com.sparta.ddang.domain.dto.ResponseDto;
import com.sparta.ddang.domain.favorite.dto.FavoriteResponseDto;
import com.sparta.ddang.domain.favorite.entity.Favorite;
import com.sparta.ddang.domain.favorite.repository.FavoriteRespository;
import com.sparta.ddang.domain.joinprice.entity.JoinPrice;
import com.sparta.ddang.domain.joinprice.repository.JoinPriceRepository;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.domain.member.repository.MemberRepository;
import com.sparta.ddang.domain.member.service.MemberService;
import com.sparta.ddang.domain.mulltiimg.awsS3exceptionhandler.FileTypeErrorException;
import com.sparta.ddang.domain.mulltiimg.entity.MultiImage;
import com.sparta.ddang.domain.mulltiimg.repository.MultiImgRepository;
import com.sparta.ddang.domain.notification.entity.NotificationType;
import com.sparta.ddang.domain.notification.service.NotificationService;
import com.sparta.ddang.domain.participant.entity.Participant;
import com.sparta.ddang.domain.participant.repository.ParticipantRepository;
import com.sparta.ddang.domain.region.dto.RegionOnlyResponseDto;
import com.sparta.ddang.domain.region.dto.RegionResponseDto;
import com.sparta.ddang.domain.region.entity.Region;
import com.sparta.ddang.domain.region.repository.RegionRepository;
import com.sparta.ddang.domain.search.dto.PopularSearchResponseDto;
import com.sparta.ddang.domain.search.dto.RecentSearchResponseDto;
import com.sparta.ddang.domain.search.entity.PopularSearch;
import com.sparta.ddang.domain.search.entity.RecentSearch;
import com.sparta.ddang.domain.search.repository.PopularSearchRepository;
import com.sparta.ddang.domain.search.repository.RecentSearchRepository;
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
import java.time.LocalDateTime;
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
    private final PopularSearchRepository popularSearchRepository;
    private final RecentSearchRepository recentSearchRepository;
    private final ChatRoomService chatRoomService;
    private final NotificationService notificationService;
    private final ChatMessageJpaRepository chatMessageJpaRepository;
    private final MemberService memberService;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷 이름

    @Transactional
    public ResponseDto<?> getAllAuction() {
        List<Auction> auctionList = auctionRepository.findAllByOrderByModifiedAtDesc();
        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        for (Auction auction : auctionList) {
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
                            .participantStatus(auction.isParticipantStatus())
                            .createdAt(auction.getCreatedAt())
                            .modifiedAt(auction.getModifiedAt())
                            .build()
            );
        }

        return ResponseDto.success(auctionResponseDtoList);
    }

    @Transactional
    public ResponseDto<?> getDetailAuction(Long auctionId, HttpServletRequest request) {
        Auction auction = checkAuction(auctionId);

        if (auction == null) {
            return ResponseDto.fail("해당 경매 게시글이 없습니다.");
        }

        Member member = validateMember(request);
        LocalDateTime now = LocalDateTime.now();
        String auctionChatRoomId = auction.getChatRoomId();
        List<ChatMessage> chatMessages = chatMessageJpaRepository.findAllByRoomId(auctionChatRoomId);
        ArrayList<String> nickChk = new ArrayList<>();

        for (ChatMessage chatMessage : chatMessages){
            if(!nickChk.contains(chatMessage.getNickName())) {
                nickChk.add(chatMessage.getNickName());
            }
        }

        int nickCnt = nickChk.size();

        if (now.isEqual(auction.getDeadline()) || now.isAfter(auction.getDeadline()) ) {
            auction.changeAuctionStatus(false);
            auctionRepository.save(auction);
        }

        Long favoriteCnt = favoriteRespository.countAllByAuctionId(auctionId);

        if (null == member) {
            return ResponseDto.success(
                    AuctionDetailResponseDto.builder()
                            .auctionId(auction.getId())
                            .productName(auction.getProductName())
                            .tags(auction.getTags())
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
                            .participantStatus(auction.isParticipantStatus())
                            .favoriteCnt(favoriteCnt)
                            .createdAt(auction.getCreatedAt())
                            .modifiedAt(auction.getModifiedAt())
                            .chatPeopleCnt(nickCnt)
                            .build()
            );
        }

        Long memId = member.getId();
        Long aucId = auctionId;
        String trustGrade = memberService.calcGrade(member.getTrustPoint());

        if (viewCntRepository.existsByMemberIdAndAuctionId(memId, aucId)) {
            if (favoriteRespository.existsByMemberIdAndAuctionId(memId,aucId)) {
                return ResponseDto.success(
                        AuctionDetailResponseDto.builder()
                                .auctionId(auction.getId())
                                .productName(auction.getProductName())
                                .tags(auction.getTags())
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
                                .participantStatus(auction.isParticipantStatus())
                                .favoriteStatus(true)
                                .favoriteCnt(favoriteCnt)
                                .roomId(auction.getChatRoomId())
                                .bidRoomId(auction.getBidRoomId())
                                .createdAt(auction.getCreatedAt())
                                .modifiedAt(auction.getModifiedAt())
                                .chatPeopleCnt(nickCnt)
                                .trustGrade(trustGrade)
                                .build()
                );

            } else {
                return ResponseDto.success(
                        AuctionDetailResponseDto.builder()
                                .auctionId(auction.getId())
                                .productName(auction.getProductName())
                                .tags(auction.getTags())
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
                                .participantStatus(auction.isParticipantStatus())
                                .favoriteStatus(false)
                                .favoriteCnt(favoriteCnt)
                                .roomId(auction.getChatRoomId())
                                .bidRoomId(auction.getBidRoomId())
                                .createdAt(auction.getCreatedAt())
                                .modifiedAt(auction.getModifiedAt())
                                .chatPeopleCnt(nickCnt)
                                .trustGrade(trustGrade)
                                .build()
                );
            }
        }

        ViewCnt viewCnt = new ViewCnt(memId, aucId);
        viewCntRepository.save(viewCnt);
        auction.cntAuction();
        auctionRepository.save(auction);

        String cate = auction.getCategory();
        Long cateCnt = auction.getViewerCnt();
        Category category = checkCategory(cate);

        if (categoryRepository.existsByCategory(cate)) {
            category.updateCateCnt(cate);
            categoryRepository.save(category);
        }

        if (category == null) {
            category = new Category(cate, cateCnt);
            categoryRepository.save(category);
        }

        String regi = auction.getRegion();
        Long regionCnt = auction.getViewerCnt();
        Region region = checkRegion(regi);

        if (regionRepository.existsByRegion(regi)) {
            region.updateRegionCnt(regi);
            regionRepository.save(region);
        }

        if (region == null) {
            region = new Region(regi, regionCnt);
            regionRepository.save(region);
        }

        return ResponseDto.success(
                AuctionDetailResponseDto.builder()
                        .auctionId(auction.getId())
                        .productName(auction.getProductName())
                        .tags(auction.getTags())
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
                        .participantStatus(auction.isParticipantStatus())
                        .favoriteStatus(false)
                        .favoriteCnt(favoriteCnt)
                        .roomId(auction.getChatRoomId())
                        .bidRoomId(auction.getBidRoomId())
                        .createdAt(auction.getCreatedAt())
                        .modifiedAt(auction.getModifiedAt())
                        .chatPeopleCnt(nickCnt)
                        .trustGrade(trustGrade)
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> createAuction(List<MultipartFile> multipartFile,
                                        AuctionRequestDto auctionRequestDto,
                                        AuctionTagsRequestDto auctionTagsRequestDto,
                                        HttpServletRequest request) throws IOException {

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }

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

            MultiImage multiImage = new MultiImage(imgUrl, auction.getId(), member.getId());
            multiImages.add(multiImage);
            imgUrlList.add(imgUrl);
            multiImgRepository.save(multiImage);

            if (categoryRepository.count() == 0) {
                Category cat0 = new Category("전체품목", 0L);
                categoryRepository.save(cat0);
                Category cat1 = new Category("가구인테리어", 0L);
                categoryRepository.save(cat1);
                Category cat2 = new Category("가전", 0L);
                categoryRepository.save(cat2);
                Category cat3 = new Category("남성패션", 0L);
                categoryRepository.save(cat3);
                Category cat4 = new Category("여성패션", 0L);
                categoryRepository.save(cat4);
                Category cat5 = new Category("악세서리", 0L);
                categoryRepository.save(cat5);
                Category cat6 = new Category("스포츠레저", 0L);
                categoryRepository.save(cat6);
                Category cat7 = new Category("취미게임악기", 0L);
                categoryRepository.save(cat7);
                Category cat8 = new Category("디지털", 0L);
                categoryRepository.save(cat8);
                Category cat9 = new Category("뷰티미용", 0L);
                categoryRepository.save(cat9);
            }

            if (regionRepository.count() == 0) {
                Region reg0 = new Region("서울전체", 0L);
                regionRepository.save(reg0);
                Region reg1 = new Region("강남구", 0L);
                regionRepository.save(reg1);
                Region reg2 = new Region("강동구", 0L);
                regionRepository.save(reg2);
                Region reg3 = new Region("강북구", 0L);
                regionRepository.save(reg3);
                Region reg4 = new Region("강서구", 0L);
                regionRepository.save(reg4);
                Region reg5 = new Region("관악구", 0L);
                regionRepository.save(reg5);
                Region reg6 = new Region("광진구", 0L);
                regionRepository.save(reg6);
                Region reg7 = new Region("구로구", 0L);
                regionRepository.save(reg7);
                Region reg8 = new Region("금천구", 0L);
                regionRepository.save(reg8);
                Region reg9 = new Region("노원구", 0L);
                regionRepository.save(reg9);
                Region reg10 = new Region("도봉구", 0L);
                regionRepository.save(reg10);
                Region reg11 = new Region("동대문구", 0L);
                regionRepository.save(reg11);
                Region reg12 = new Region("동작구", 0L);
                regionRepository.save(reg12);
                Region reg13 = new Region("마포구", 0L);
                regionRepository.save(reg13);
                Region reg14 = new Region("서대문구", 0L);
                regionRepository.save(reg14);
                Region reg15 = new Region("서초구", 0L);
                regionRepository.save(reg15);
                Region reg16 = new Region("성동구", 0L);
                regionRepository.save(reg16);
                Region reg17 = new Region("성북구", 0L);
                regionRepository.save(reg17);
                Region reg18 = new Region("송파구", 0L);
                regionRepository.save(reg18);
                Region reg19 = new Region("양천구", 0L);
                regionRepository.save(reg19);
                Region reg20 = new Region("영등포구", 0L);
                regionRepository.save(reg20);
                Region reg21 = new Region("용산구", 0L);
                regionRepository.save(reg21);
                Region reg22 = new Region("은평구", 0L);
                regionRepository.save(reg22);
                Region reg23 = new Region("종로구", 0L);
                regionRepository.save(reg23);
                Region reg25 = new Region("중구", 0L);
                regionRepository.save(reg25);
                Region reg26 = new Region("중랑구", 0L);
                regionRepository.save(reg26);
            }
        }

        String auchatName = "경매" + auction.getId() + "번방";
        ChatRoomDto chatRoomDto = chatRoomService.createRoom(auchatName);
        auction.addAuctionChatRoomId(chatRoomDto.getRoomId());

        String aucBidName = "경매 호가" + auction.getId() + "번방";
        ChatRoomDto chatRoomDto1 = chatRoomService.createRoom(aucBidName);
        auction.addAuctionBidRoomId(chatRoomDto1.getRoomId());

        auctionRepository.save(auction);

        List<Member> receiverList = memberRepository.findAll();
        for (Member receiver : receiverList) {
            log.info("받는사람 닉네임 >>> "+receiver.getNickName());
            notificationService.send(receiver, NotificationType.BID, "새로운 경매가 시작되었습니다!");
        }

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

        auctionRepository.deleteById(auctionId);

        return ResponseDto.successToMessage(200, "게시물이 성공적으로 삭제되었습니다", null);
    }

    @Transactional
    public ResponseDto<?> findCategoryAuction(String category) {
        List<Auction> auctionList = auctionRepository.findAllByCategory(category);
        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        for (Auction auction : auctionList) {
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
                            .participantStatus(false)
                            .createdAt(auction.getCreatedAt())
                            .modifiedAt(auction.getModifiedAt())
                            .build()
            );
        }

        return ResponseDto.success(auctionResponseDtoList);
    }

    @Transactional
    public ResponseDto<?> findRegionAuction(String region) {
        List<Auction> auctionList = auctionRepository.findAllByRegion(region);
        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        for (Auction auction : auctionList) {
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
                            .participantStatus(false)
                            .createdAt(auction.getCreatedAt())
                            .modifiedAt(auction.getModifiedAt())
                            .build()
            );
        }

        return ResponseDto.success(auctionResponseDtoList);
    }

    @Transactional
    public ResponseDto<?> findCategoryAndRegionAuction(String category, String region) {
        List<Auction> auctionList = auctionRepository.findAllByCategoryAndRegion(category, region);
        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        for (Auction auction : auctionList) {
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
                            .participantStatus(false)
                            .createdAt(auction.getCreatedAt())
                            .modifiedAt(auction.getModifiedAt())
                            .build()
            );
        }

        return ResponseDto.success(auctionResponseDtoList);
    }

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

        if (participantRepository.existsByMemberIdAndAuctionId(member.getId(), auctionId)) {
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
                            .participantStatus(false)
                            .createdAt(auction.getCreatedAt())
                            .modifiedAt(auction.getModifiedAt())
                            .build()
            );
        }

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
                        .participantStatus(true)
                        .createdAt(auction.getCreatedAt())
                        .modifiedAt(auction.getModifiedAt())
                        .build()
        );
    }

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

        List<Participant> participantList = participantRepository.findAllByMemberId(member.getId());
        ArrayList<AuctionResponseDto> auctionArrayList = new ArrayList<>();

        for (Participant participant : participantList) {

            Auction auction = auctionRepository.findById(participant.getAuctionId()).orElse(null);

            if (auctionRepository.existsById(participant.getAuctionId())) {

                auctionArrayList.add(
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
                                .reviewDone(auction.isReviewDone())
                                .createdAt(auction.getCreatedAt())
                                .modifiedAt(auction.getModifiedAt())
                                .build()
                );
//            auctionArrayList.add(
//                    AuctionResponseDto.builder()
//                            .auctionId(participant.getAuction().getId())
//                            .memberId(participant.getAuction().getMember().getId())
//                            .nickname(participant.getAuction().getMember().getNickName())
//                            .profileImgUrl(participant.getAuction().getMember().getProfileImgUrl())
//                            .title(participant.getAuction().getTitle())
//                            .content(participant.getAuction().getContent())
//                            .multiImages(participant.getAuction().getMultiImages())
//                            .startPrice(participant.getAuction().getStartPrice())
//                            .nowPrice(participant.getAuction().getNowPrice())
//                            .auctionPeriod(participant.getAuction().getAuctionPeriod())
//                            .category(participant.getAuction().getCategory())
//                            .region(participant.getAuction().getRegion())
//                            .direct(participant.getAuction().isDirect())
//                            .delivery(participant.getAuction().isDelivery())
//                            .viewerCnt(participant.getAuction().getViewerCnt())
//                            .participantCnt(participant.getAuction().getParticipantCnt())
//                            .participantStatus(participant.getAuction().isParticipantStatus())
//                            .auctionStatus(participant.getAuction().isAuctionStatus())
//                            .reviewDone(participant.getAuction().isReviewDone())
//                            .createdAt(participant.getAuction().getCreatedAt())
//                            .modifiedAt(participant.getAuction().getModifiedAt())
//                            .build()
//            );
            }

        }

        return ResponseDto.success(auctionArrayList);
    }

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

        if (favoriteRespository.existsByMemberIdAndAuctionId(member.getId(), auctionId)) {
            favoriteRespository.deleteByMemberIdAndAuctionId(member.getId(), auctionId);

            return ResponseDto.success(
                    FavoriteResponseDto.builder()
                            .auctionId(auction.getId())
                            .memberId(auction.getMember().getId())
                            .nickname(member.getNickName())
                            .favoriteStatus(false)
                            .build()
            );
        }

        Favorite favorite = new Favorite(member, auction);
        favoriteRespository.save(favorite);

        return ResponseDto.success(
                FavoriteResponseDto.builder()
                        .auctionId(auction.getId())
                        .memberId(auction.getMember().getId())
                        .nickname(member.getNickName())
                        .favoriteStatus(true)
                        .build()
        );
    }

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

        for (Favorite favorite : favorites) {
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

        for (Auction auction : auctionList) {
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
                            .reviewDone(auction.isReviewDone())
                            .createdAt(auction.getCreatedAt())
                            .modifiedAt(auction.getModifiedAt())
                            .build()

            );
        }

        return ResponseDto.success(auctionResponseDtoList);
    }

    @Transactional
    public ResponseDto<?> getAllHitCategory() {
        if (categoryRepository.count() == 0) {
            return ResponseDto.fail("카테고리가 없습니다.");
        }

        List<Category> categoryList = categoryRepository.findAllByOrderByViewerCntDesc();
        List<CategoryResponseDto> categories = new ArrayList<>();

        for (Category category : categoryList) {
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

        for (Category category : categories) {
            categoryOnlyResponseDtos.add(
                    CategoryOnlyResponseDto.builder()
                            .categoryName(category.getCategory())
                            .build()
            );
        }

        return ResponseDto.success(categoryOnlyResponseDtos);
    }

    @Transactional
    public ResponseDto<?> getAllHitRegion() {
        if (regionRepository.count() == 0) {
            return ResponseDto.fail("지역이 없습니다.");

        }

        List<Region> regionList = regionRepository.findAllByOrderByViewerCntDesc();
        List<RegionResponseDto> regions = new ArrayList<>();

        for (Region region : regionList) {
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

        for (Region region : regions) {
            regionOnlyResponseDtos.add(
                    RegionOnlyResponseDto.builder()
                            .region(region.getRegion())
                            .build()

            );
        }

        return ResponseDto.success(regionOnlyResponseDtos);
    }

    @Transactional
    public ResponseDto<?> getSearchTitle(String title, HttpServletRequest request) {
        Member member = validateMember(request);
        if (null == member) {
            List<Auction> auctionList = auctionRepository.findByTitleContaining(title);
            List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

            LocalDateTime now = LocalDateTime.now();

            for (Auction auction : auctionList) {
                if (now.isEqual(auction.getDeadline()) || now.isAfter(auction.getDeadline()) ) {
                    auction.changeAuctionStatus(false);
                    auctionRepository.save(auction);

                }

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

            if (!popularSearchRepository.existsBySearchWord(title)) {
                PopularSearch popularSearch = new PopularSearch(title);
                popularSearchRepository.save(popularSearch);
            } else {
                PopularSearch popularSearch = popularSearchRepository.findBySearchWord(title);
                popularSearch.addSearchWordCnt();
                popularSearchRepository.save(popularSearch);
            }

            return ResponseDto.success(auctionResponseDtoList);
        }

        List<Auction> auctionList = auctionRepository.findByTitleContaining(title);
        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (Auction auction : auctionList) {
            if (now.isEqual(auction.getDeadline()) || now.isAfter(auction.getDeadline()) ) {
                auction.changeAuctionStatus(false);
                auctionRepository.save(auction);
            }

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

        if (!recentSearchRepository.existsByMemberIdAndSearchWord(member.getId(), title)){
            RecentSearch recentSearch = new RecentSearch(member.getId(), title);
            recentSearchRepository.save(recentSearch);
        } else {
            RecentSearch recentSearch = recentSearchRepository.findByMemberIdAndSearchWord(member.getId(), title);
            LocalDateTime recentSearchNow = LocalDateTime.now();
            recentSearch.updateTime(recentSearchNow);
        }

        if (!popularSearchRepository.existsBySearchWord(title)) {
            PopularSearch popularSearch = new PopularSearch(title);
            popularSearchRepository.save(popularSearch);
        } else {
            PopularSearch popularSearch = popularSearchRepository.findBySearchWord(title);
            popularSearch.addSearchWordCnt();
            popularSearchRepository.save(popularSearch);
        }

        return ResponseDto.success(auctionResponseDtoList);
    }

    @Transactional
    public ResponseDto<?> getSearchRecent(HttpServletRequest request) {
        Member member = validateMember(request);
        if (member == null){
            return ResponseDto.success("회원에게만 제공되는 서비스입니다.");
        }

        List<RecentSearch> recentSearches = recentSearchRepository.findAllByMemberIdOrderByModifiedAtDesc(member.getId());
        List<RecentSearchResponseDto> recentSearchResponseDtos = new ArrayList<>();

        for (RecentSearch recentSearch : recentSearches){
            recentSearchResponseDtos.add(
                    RecentSearchResponseDto.builder()
                            .searchWord(recentSearch.getSearchWord())
                            .searchTime(recentSearch.getModifiedAt())
                            .build()
            );

            if (recentSearchResponseDtos.size() >= 10) break;
        }

        return ResponseDto.success(recentSearchResponseDtos);
    }

    @Transactional
    public ResponseDto<?> getSearchPopular() {
        List<PopularSearch> popularSearches = popularSearchRepository.findAllByOrderBySearchWordCntDesc();
        List<PopularSearchResponseDto> popularSearchResponseDtos = new ArrayList<>();

        for (PopularSearch popularSearch : popularSearches){
            popularSearchResponseDtos.add(
                    PopularSearchResponseDto.builder()
                            .searchWord(popularSearch.getSearchWord())
                            .searchWordCnt(popularSearch.getSearchWordCnt())
                            .build()
            );

            if (popularSearchResponseDtos.size() >= 10) break;
        }

        return ResponseDto.success(popularSearchResponseDtos);
    }

    @Transactional
    public ResponseDto<?> getBidder(Long auctionId) {
        Auction auction = checkAuction(auctionId);
        Member seller = auction.getMember();

        List<JoinPrice> joinPriceList = joinPriceRepository.findAllByAuctionIdOrderByJoinPriceDesc(auctionId);
        JoinPrice joinPrice = joinPriceList.get(0);
        Member bidder = checkMember(joinPrice.getMemberId());

        ChatRoomDto chatRoomDto = chatRoomService.createRoom("경매" + auctionId + "방 1:1 채팅방");
        auction.addAuctionOnoRoomId(chatRoomDto.getRoomId());

        auction.changeAuctionStatus(false);
        auctionRepository.save(auction);

        return ResponseDto.success(
                BidderResponseDto.builder()
                        .auctionId(auctionId)
                        .seller(seller.getNickName())
                        .bidder(bidder.getNickName())
                        .roomId(chatRoomDto.getRoomId())
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> getAuctionTop4() {
        List<Auction> auctionList = auctionRepository.findAllByOrderByViewerCntDesc();
        List<AuctionRankResponseDto> auctionRankResponseDtos = new ArrayList<>();
        List<AuctionRankResponseDto> auctionRankResponseDtoList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (Auction auction : auctionList) {
            if (now.isEqual(auction.getDeadline()) || now.isAfter(auction.getDeadline()) ) {
                auction.changeAuctionStatus(false);
                auctionRepository.save(auction);
                continue;
            }

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

        if (auctionRankResponseDtos.size() == 0) {
            return ResponseDto.fail("게시글이 없습니다. 게시글을 등록해주세요");
        }

        if (auctionRankResponseDtos.size() == 1) {
            for (int i = 0; i < 1; i++) {
                auctionRankResponseDtoList.add(
                        auctionRankResponseDtos.get(i)
                );
            }

            return ResponseDto.success(auctionRankResponseDtoList);
        }

        if (auctionRankResponseDtos.size() == 2) {
            for (int i = 0; i < 2; i++) {
                auctionRankResponseDtoList.add(
                        auctionRankResponseDtos.get(i)
                );
            }

            return ResponseDto.success(auctionRankResponseDtoList);
        }

        if (auctionRankResponseDtos.size() == 3) {
            for (int i = 0; i < 3; i++) {
                auctionRankResponseDtoList.add(
                        auctionRankResponseDtos.get(i)
                );
            }

            return ResponseDto.success(auctionRankResponseDtoList);
        }

        for (int i = 0; i < 4; i++) {
            auctionRankResponseDtoList.add(
                    auctionRankResponseDtos.get(i)
            );
        }

        return ResponseDto.success(auctionRankResponseDtoList);
    }

    @Transactional
    public ResponseDto<?> getNewReleaseTop3() {
        List<Auction> auctionList = auctionRepository.findAllByOrderByCreatedAtDesc();
        List<AuctionRankResponseDto> auctionRankResponseDtos = new ArrayList<>();
        List<AuctionRankResponseDto> auctionRankResponseDtoList = new ArrayList<>();

        for (Auction auction : auctionList) {
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

        if (auctionRankResponseDtos.size() == 0) {
            return ResponseDto.fail("게시글이 없습니다. 게시글을 등록해주세요");
        }

        if (auctionRankResponseDtos.size() == 1) {
            for (int i = 0; i < 1; i++) {
                auctionRankResponseDtoList.add(
                        auctionRankResponseDtos.get(i)
                );
            }

            return ResponseDto.success(auctionRankResponseDtoList);
        }

        if (auctionRankResponseDtos.size() == 2) {
            for (int i = 0; i < 2; i++) {
                auctionRankResponseDtoList.add(
                        auctionRankResponseDtos.get(i)
                );
            }

            return ResponseDto.success(auctionRankResponseDtoList);
        }

        for (int i = 0; i < 3; i++) {
            auctionRankResponseDtoList.add(
                    auctionRankResponseDtos.get(i)
            );
        }

        return ResponseDto.success(auctionRankResponseDtoList);
    }

    @Transactional
    public ResponseDto<?> getDeadlineAuctions() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> auctions = auctionRepository.findAllByOrderByDeadlineAsc();
        List<DeadlineAuctionResponseDto> deadlineAuctionResponseDtoList = new ArrayList<>();

        for (Auction auction : auctions) {
            if (now.isEqual(auction.getDeadline()) || now.isAfter(auction.getDeadline()) ) {
                auction.changeAuctionStatus(false);
                auctionRepository.save(auction);
            }

            if (now.isBefore(auction.getDeadline()) && auction.isAuctionStatus() == true) {
                deadlineAuctionResponseDtoList.add(
                        DeadlineAuctionResponseDto.builder()
                                .title(auction.getTitle())
                                .content(auction.getContent())
                                .nowPrice(auction.getNowPrice())
                                .multiImages(auction.getMultiImages())
                                .memberId(auction.getMember().getId())
                                .auctionId(auction.getId())
                                .direct(auction.isDirect())
                                .delivery(auction.isDelivery())
                                .region(auction.getRegion())
                                .deadline(auction.getDeadline())
                                .build()
                );
            }

            if (deadlineAuctionResponseDtoList.size() >= 4) break;
        }

        return ResponseDto.success(deadlineAuctionResponseDtoList);
    }

    @Transactional
    public ResponseDto<?> reviewAuction(Long auctionId, ReviewRequestDto reviewRequestDto, HttpServletRequest request) {
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }

        Auction auction = checkAuction(auctionId);
        Member seller = auction.getMember();

        List<JoinPrice> joinPriceList = joinPriceRepository.findAllByAuctionIdOrderByJoinPriceDesc(auctionId);
        JoinPrice joinPrice = joinPriceList.get(0);
        Member bidder = checkMember(joinPrice.getMemberId());

        if (member.getId().equals(seller.getId())) {
            bidder.updateTrustPoint(reviewRequestDto.getTrustPoint());
            memberRepository.save(bidder);
            auction.changeSellerDone();
            auctionRepository.save(auction);
            return ResponseDto.success("판매자가 낙찰자 평가하기 완료");
        }

        if (member.getId().equals(bidder.getId())) {
            seller.updateTrustPoint(reviewRequestDto.getTrustPoint());
            memberRepository.save(seller);
            auction.changeBidderDone();
            auctionRepository.save(auction);
            return ResponseDto.success("낙찰자가 판매자 평가하기 완료");
        }

        if (auction.isSellerDone() && auction.isBidderDone()) {
            auction.changeReviewDone();
            auctionRepository.save(auction);
        }

        return ResponseDto.fail("문제가 발생했습니다.");
    }

    @Transactional
    public ResponseDto<?> doneAuction(Long auctionId, HttpServletRequest request) {
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }

        Auction auction = checkAuction(auctionId);
        boolean isSeller = member.getId().equals(auction.getMember().getId());
        DoneAuctionResponseDto doneAuctionResponseDto = new DoneAuctionResponseDto(auction.getId(), auction.isSellerDone(), auction.isBidderDone(), isSeller);

        return ResponseDto.success(doneAuctionResponseDto);
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

    @Transactional(readOnly = true)
    public Category checkCategory(String cate) {
        Optional<Category> optionalCategory = categoryRepository.findByCategory(cate);
        return optionalCategory.orElse(null);
    }

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

    private Optional<File> convert(MultipartFile file) throws IOException {
        String type = file.getContentType();
        long size = file.getSize();

        if (!type.startsWith("image")) {
            throw new FileTypeErrorException();
        }

        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());

        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }

            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    public String upload(File uploadFile) {
        String fileName = "DdangDdang/auctionImg/" + UUID.randomUUID() + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        log.info(uploadImageUrl);
        removeNewFile(uploadFile);

        return uploadImageUrl;
    }

    public String upload(MultipartFile multipartFile) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("error: 파일 변환에 실패했습니다"));

        return upload(uploadFile);
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }

}

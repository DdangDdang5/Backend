package com.sparta.ddang.domain.pagination.service;


import com.sparta.ddang.domain.auction.dto.resposne.AuctionResponseDto;
import com.sparta.ddang.domain.auction.entity.Auction;
import com.sparta.ddang.domain.auction.repository.AuctionRepository;
import com.sparta.ddang.domain.dto.ResponseDto;
import com.sparta.ddang.domain.favorite.entity.Favorite;
import com.sparta.ddang.domain.favorite.repository.FavoriteRespository;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.domain.participant.entity.Participant;
import com.sparta.ddang.domain.participant.repository.ParticipantRepository;
import com.sparta.ddang.jwt.TokenProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Service
public class PagingService {


    private final AuctionRepository auctionRepository;

    private final TokenProvider tokenProvider;

    private final ParticipantRepository participantRepository;

    private final FavoriteRespository favoriteRespository;


    public PagingService(AuctionRepository auctionRepository,TokenProvider tokenProvider,
                         ParticipantRepository participantRepository,
                         FavoriteRespository favoriteRespository){

        this.auctionRepository = auctionRepository;
        this.tokenProvider = tokenProvider;
        this.participantRepository = participantRepository;
        this.favoriteRespository = favoriteRespository;

    }

    // 경매 전체 조회 페이지네이션
    @Transactional
    public ResponseDto<?> getAuctionPagenation(int page, int size, String sortBy, boolean isAsc) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size,sort);

        Page<Auction> auctions = auctionRepository.findAll(pageable);

        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        for (Auction auction : auctions){

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

        return ResponseDto.success(auctionResponseDtoList);

    }

    // 경매 카테고리 조회 페이지네이션
    @Transactional
    public ResponseDto<?> getCategoryPagenation(String category,int page, int size,
                                                String sortBy, boolean isAsc) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size,sort);

        Page<Auction> auctions = auctionRepository.findAllByCategory(category,pageable);

        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        for (Auction auction : auctions){

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

            return ResponseDto.success(auctionResponseDtoList);

    }

    public ResponseDto<?> getRegionPagenation(String region, int page,
                                              int size, String sortBy,
                                              boolean isAsc) {


        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size,sort);

        Page<Auction> auctions = auctionRepository.findAllByRegion(region,pageable);

        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        for (Auction auction : auctions){

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

        return ResponseDto.success(auctionResponseDtoList);


    }
    // 경매 카테고리 및 지역별 조회 페이지네이션
    @Transactional
    public ResponseDto<?> getCateRegiPagenation(String category, String region,
                                                int page, int size, String sortBy,
                                                boolean isAsc) {


        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size,sort);

        Page<Auction> auctions = auctionRepository.findAllByCategoryAndRegion(category,region,pageable);

        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        for (Auction auction : auctions){

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

        return ResponseDto.success(auctionResponseDtoList);

    }
    
    // 내가 참여한 경매 페이지네이션
    @Transactional
    public ResponseDto<?> getJoinPagenation(HttpServletRequest request,
                                            int page, int size, String sortBy,
                                            boolean isAsc) {


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

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size,sort);

        Page<Participant> participantList = participantRepository.findAllByMember_Id(member.getId(),pageable);

        List<AuctionResponseDto> auctionArrayList = new ArrayList<>();

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

    // 내가 찜한 경매 조회 페이지 네이션
    @Transactional
    public ResponseDto<?> getMyFavoritePagenation(HttpServletRequest request,
                                                  int page, int size,
                                                  String sortBy, boolean isAsc) {

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("Authorization이 없습니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size,sort);

        Page<Favorite> favorites = favoriteRespository.findAllByMember_Id(member.getId(),pageable);

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
    
    // 내가 시작한 경매 페이지네이션
    @Transactional
    public ResponseDto<?> getMyAuctionPagenation(HttpServletRequest request, int page,
                                                 int size, String sortBy,
                                                 boolean isAsc) {

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("Authorization이 없습니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size,sort);

        Page<Auction> auctionList = auctionRepository.findAllByMember_Id(member.getId(),pageable);

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

        return ResponseDto.success(auctionResponseDtoList);

    }







    //======================== 회원 정보 및 경매 정보 ========================

    @Transactional
    public Member validateMember(HttpServletRequest request) {

        return tokenProvider.getMemberFromAuthentication();
    }

    
   
}
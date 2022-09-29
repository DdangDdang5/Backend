package com.sparta.ddang.domain.pagination.service;


import com.sparta.ddang.domain.auction.dto.resposne.AuctionResponseDto;
import com.sparta.ddang.domain.auction.dto.resposne.ParticipantAuctionResponseDto;
import com.sparta.ddang.domain.auction.entity.Auction;
import com.sparta.ddang.domain.auction.repository.AuctionRepository;
import com.sparta.ddang.domain.auction.service.AuctionService;
import com.sparta.ddang.domain.dto.ResponseDto;
import com.sparta.ddang.domain.favorite.entity.Favorite;
import com.sparta.ddang.domain.favorite.repository.FavoriteRespository;
import com.sparta.ddang.domain.joinprice.entity.JoinPrice;
import com.sparta.ddang.domain.joinprice.repository.JoinPriceRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class PagingService {


    private final AuctionRepository auctionRepository;

    private final TokenProvider tokenProvider;

    private final ParticipantRepository participantRepository;

    private final FavoriteRespository favoriteRespository;

    private final AuctionService auctionService;

    private final JoinPriceRepository joinPriceRepository;


    public PagingService(AuctionRepository auctionRepository, TokenProvider tokenProvider,
                         ParticipantRepository participantRepository,
                         FavoriteRespository favoriteRespository,
                         AuctionService auctionService,
                         JoinPriceRepository joinPriceRepository) {

        this.auctionRepository = auctionRepository;
        this.tokenProvider = tokenProvider;
        this.participantRepository = participantRepository;
        this.favoriteRespository = favoriteRespository;
        this.auctionService = auctionService;
        this.joinPriceRepository = joinPriceRepository;

    }

    // 경매 전체 조회 페이지네이션
    @Transactional
    public ResponseDto<?> getAuctionPagenation(int page, int size, String sortBy, boolean isAsc) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Auction> auctions = auctionRepository.findAll(pageable);

        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now(); // 클라이언트에서 api를 호출한 시간(현재 기준 시간)

        for (Auction auction : auctions) {

            // 마감입박시간이거나 마감임박시간 이후 일 경우 auctionstatus를 false로 바꿈
            if (now.isEqual(auction.getDeadline()) || now.isAfter(auction.getDeadline())) {

                auction.changeAuctionStatus(false);

                auctionRepository.save(auction);

            }


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
    public ResponseDto<?> getCategoryPagenation(String category, int page, int size,
                                                String sortBy, boolean isAsc) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Auction> auctions = auctionRepository.findAllByCategory(category, pageable);

        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now(); // 클라이언트에서 api를 호출한 시간(현재 기준 시간)

        for (Auction auction : auctions) {

            // 마감입박시간이거나 마감임박시간 이후 일 경우 auctionstatus를 false로 바꿈
            if (now.isEqual(auction.getDeadline()) || now.isAfter(auction.getDeadline())) {

                auction.changeAuctionStatus(false);

                auctionRepository.save(auction);

            }

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
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Auction> auctions = auctionRepository.findAllByRegion(region, pageable);

        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now(); // 클라이언트에서 api를 호출한 시간(현재 기준 시간)

        for (Auction auction : auctions) {

            // 마감입박시간이거나 마감임박시간 이후 일 경우 auctionstatus를 false로 바꿈
            if (now.isEqual(auction.getDeadline()) || now.isAfter(auction.getDeadline())) {

                auction.changeAuctionStatus(false);

                auctionRepository.save(auction);

            }

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
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Auction> auctions = auctionRepository.findAllByCategoryAndRegion(category, region, pageable);

        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now(); // 클라이언트에서 api를 호출한 시간(현재 기준 시간)

        for (Auction auction : auctions) {

            // 마감입박시간이거나 마감임박시간 이후 일 경우 auctionstatus를 false로 바꿈
            if (now.isEqual(auction.getDeadline()) || now.isAfter(auction.getDeadline())) {

                auction.changeAuctionStatus(false);

                auctionRepository.save(auction);

            }

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
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Participant> participantList = participantRepository.findAllByMember_Id(member.getId(), pageable);

        List<ParticipantAuctionResponseDto> auctionArrayList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now(); // 클라이언트에서 api를 호출한 시간(현재 기준 시간)

        for (Participant participant : participantList) {

            // 마감입박시간이거나 마감임박시간 이후 일 경우 auctionstatus를 false로 바꿈
            if (now.isEqual(participant.getAuction().getDeadline()) || now.isAfter(participant.getAuction().getDeadline()) ) {

                participant.getAuction().changeAuctionStatus(false);

                auctionRepository.save(participant.getAuction());

            }

            Long auctionId = participant.getAuction().getId();
            List<JoinPrice> joinPriceList = joinPriceRepository.findAllByAuctionIdOrderByJoinPriceDesc(auctionId);
            JoinPrice joinPrice = joinPriceList.get(0);
            Member bidder = auctionService.checkMember(joinPrice.getMemberId());
            boolean isBidder = member.getId().equals(bidder.getId());

            auctionArrayList.add(
                    ParticipantAuctionResponseDto.builder()
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
                            .onoRoomId(participant.getAuction().getOnoRoomId())
                            .isBidder(isBidder)
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
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Favorite> favorites = favoriteRespository.findAllByMember_Id(member.getId(), pageable);

        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now(); // 클라이언트에서 api를 호출한 시간(현재 기준 시간)

        for (Favorite favorite : favorites) {

            // 마감입박시간이거나 마감임박시간 이후 일 경우 auctionstatus를 false로 바꿈
            if (now.isEqual(favorite.getAuction().getDeadline()) || now.isAfter(favorite.getAuction().getDeadline()) ) {

                favorite.getAuction().changeAuctionStatus(false);

                auctionRepository.save(favorite.getAuction());

            }

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
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Auction> auctionList = auctionRepository.findAllByMember_Id(member.getId(), pageable);

        List<AuctionResponseDto> auctionResponseDtoList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now(); // 클라이언트에서 api를 호출한 시간(현재 기준 시간)

        for (Auction auction : auctionList) {

            // 마감입박시간이거나 마감임박시간 이후 일 경우 auctionstatus를 false로 바꿈
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
                            .onoRoomId(auction.getOnoRoomId())
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

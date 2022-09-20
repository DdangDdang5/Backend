package com.sparta.ddang.domain.auction.controller;

import com.sparta.ddang.domain.auction.dto.request.AuctionRequestDto;
import com.sparta.ddang.domain.auction.dto.request.AuctionTagsRequestDto;
import com.sparta.ddang.domain.auction.dto.request.AuctionUpdateRequestDto;
import com.sparta.ddang.domain.auction.dto.request.JoinPriceRequestDto;
import com.sparta.ddang.domain.auction.service.AuctionService;
import com.sparta.ddang.domain.chat.service.ChatService;
import com.sparta.ddang.domain.dto.ResponseDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
public class AuctionController {

    private final AuctionService auctionService;

    private final ChatService chatService;

    public AuctionController(AuctionService auctionService, ChatService chatService){

        this.auctionService= auctionService;
        this.chatService = chatService;

    }

    // 경매 전체 조회
    @RequestMapping(value = "/auction", method = RequestMethod.GET)
    public ResponseDto<?> getAllAuction(){

        return auctionService.getAllAuction();

    }

    // 경매 상세 조회
    @RequestMapping(value = "/auction/{auctionId}", method = RequestMethod.GET)
    public ResponseDto<?> getDetailAuction(@PathVariable Long auctionId,
                                           HttpServletRequest request){

        return auctionService.getDetailAuction(auctionId,request);

    }
    // 토큰값 없어도 됨.
    // 경매 생성
    @RequestMapping(value = "/auction", method = RequestMethod.POST)
    public ResponseDto<?> createAuction(@RequestPart(value = "images",required = false) List<MultipartFile> multipartFile,
                                        @RequestPart(value = "auctionRequestDto")AuctionRequestDto auctionRequestDto,
                                        @RequestPart(value = "tags") AuctionTagsRequestDto auctionTagsRequestDto,
                                        HttpServletRequest request) throws IOException {
        return auctionService.createAuction(multipartFile, auctionRequestDto,auctionTagsRequestDto,request);

    }
    // 경매 수정
    @RequestMapping(value = "/auction/{auctionId}", method = RequestMethod.PATCH)
    public ResponseDto<?> updateAuction(@RequestPart(value = "images",required = false) List<MultipartFile> multipartFile,
                                        @RequestPart(value = "auctionUpdateDto") AuctionUpdateRequestDto auctionUpdateRequestDto,
                                        @RequestPart(value = "tags") AuctionTagsRequestDto auctionTagsRequestDto,
                                        @PathVariable Long auctionId,
                                        HttpServletRequest request) throws IOException {


        return auctionService.updateAuction(multipartFile,auctionId,auctionUpdateRequestDto,auctionTagsRequestDto,request);

    }
    
    // 경매 게시글 삭제
    @RequestMapping(value = "/auction/{auctionId}", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteAuction( @PathVariable Long auctionId,
                                         HttpServletRequest request){

        return auctionService.deleteAuction(auctionId,request);

    }

    // 경매 카테고리별 조회 --> 비로그인 회원도 조회가능하게 함.
    ///auction/category/{category} GET
    @RequestMapping(value = "/auction/category/{category}", method = RequestMethod.GET)
    public ResponseDto<?> findCategoryAuction(@PathVariable String category){

        return auctionService.findCategoryAuction(category);

    }

    @RequestMapping(value = "/auction/category/show", method = RequestMethod.GET)
    public ResponseDto<?> showCategoryAuction(){

        return auctionService.showCategoryAuction();

    }

    // 경매 지역별 조회 --> 비로그인 회원도 조회가능하게 함.
    @RequestMapping(value = "/auction/region/{region}", method = RequestMethod.GET)
    public ResponseDto<?> findRegionAuction(@PathVariable String region){

        return auctionService.findRegionAuction(region);

    }


    @RequestMapping(value = "/auction/region/show", method = RequestMethod.GET)
    public ResponseDto<?> showRegionAuction(){

        return auctionService.showRegionAuction();

    }

    // 경매 카테고리 & 지역별 조회 --> 비로그인 회원도 조회가능하게 함.
    // /auction/category/{category}/region/{region}
    @RequestMapping(value = "/auction/category/{category}/region/{region}", method = RequestMethod.GET)
    public ResponseDto<?> findCategoryAndRegionAuction(@PathVariable String category,
                                                       @PathVariable String region){

        return auctionService.findCategoryAndRegionAuction(category,region);
    }

    // 경매 참여
    // /auction/{auctionId}/join
    @RequestMapping(value = "/auction/{auctionId}/join", method = RequestMethod.POST)
    public ResponseDto<?> joinAuction(@PathVariable Long auctionId,
                                         @RequestBody JoinPriceRequestDto joinPriceRequestDto,
                                         HttpServletRequest request){

        return auctionService.joinAuction(auctionId,joinPriceRequestDto,request);


    }

    // 내가 참여중인 경매 조회
    // /member/{memberId}/mypage/participant
    @RequestMapping(value = "/member/mypage/participant",
            method = RequestMethod.GET)
    public ResponseDto<?> getAlljoinAuction(HttpServletRequest request){


        return auctionService.getAlljoinAuction(request);

    }

    // 관심있는 경매 찜하기
    // /auction/{auctionId}/favorite
    @RequestMapping(value = "/auction/{auctionId}/favorite",
            method = RequestMethod.GET)
    public ResponseDto<?> addfavoriteAuction(@PathVariable Long auctionId,
                                      HttpServletRequest request){

        return auctionService.addfavoriteAuction(auctionId,request);


    }

    // 내가 관심 있는 경매
    // /member/{memberId}/favorite
    @RequestMapping(value = "/member/favorite", method = RequestMethod.GET)
    public ResponseDto<?> myfavoriteAuction(HttpServletRequest request){

        return auctionService.myfavoriteAuction(request);


    }

    // 내가 시작한 경매
    // /member/{memberId}/mypage/myauction
        @RequestMapping(value = "/member/mypage/myauction", method = RequestMethod.GET)
        public ResponseDto<?> getMyAuction(HttpServletRequest request){

            return auctionService.getMyAuction(request);

        }

    // 카테고리별 인기순 조회
    // /category/hit
    @RequestMapping(value = "/category/hit", method = RequestMethod.GET)
    public ResponseDto<?> getAllHitCategory(){

        return auctionService.getAllHitCategory();

    }

    // 지역별 인기순 조회
    // /region/hit
    @RequestMapping(value = "/region/hit", method = RequestMethod.GET)
    public ResponseDto<?> getAllHitRegion(){

        return auctionService.getAllHitRegion();

    }

    // 경매 타이틀 검색 원본
//    @RequestMapping(value = "/auction/search/{title}", method = RequestMethod.GET)
//    public ResponseDto<?> getSearchTitle(@PathVariable String title){
//
//        return auctionService.getSearchTitle(title);
//
//    }

    // 경매 제목(타이틀) 검색 원본
    @RequestMapping(value = "/auction/search/{title}", method = RequestMethod.GET)
    public ResponseDto<?> getSearchTitle(@PathVariable String title,
                                         HttpServletRequest request){

        return auctionService.getSearchTitle(title,request);

    }

    // 경매 제목(타이틀) 최근순(최근검색했어요)
    @RequestMapping(value = "/auction/recent-search", method = RequestMethod.GET)
    public ResponseDto<?> getSearchRecent(HttpServletRequest request){

        return auctionService.getSearchRecent(request);

    }


    // 경매 제목(타이틀) 인기순(지금인기있어요)
    @RequestMapping(value = "/auction/popular-search", method = RequestMethod.GET)
    public ResponseDto<?> getSearchPopular(){

        return auctionService.getSearchPopular();

    }


    //낙찰자 조회
    @RequestMapping(value = "/auction/{auctionId}/bidder", method = RequestMethod.GET)
    public ResponseDto<?> getBidder(@PathVariable Long auctionId){

        return auctionService.getBidder(auctionId);

    }

    // 경매 top4 조회
    @RequestMapping(value = "/auction/hit", method = RequestMethod.GET)
    public ResponseDto<?> getAuctionTop4(){

        return auctionService.getAuctionTop4();

    }

    //최신 경매 조회(3개)
    // /auction/new-release
    @RequestMapping(value = "/auction/new-release", method = RequestMethod.GET)
    public ResponseDto<?> getNewReleaseTop3(){

        return auctionService.getNewReleaseTop3();

    }

    // 요구사항
    //1:1채팅에 참여하고 있는 해당 회원의 채팅방 목록 조회
    //- 룸아이디, 룸네임, 프로필이미지,
    //마지막 채팅대화(createdAt)
    //--> 소켓이 아닌 리스폰스 값으로 반환하기

    // 1:1채팅 방 목록 -> 해당 메시지를 전달하는 것과 같음.
    @GetMapping("/ono/{nickname}")
    public ResponseDto<?> getOnoMessage(@PathVariable String nickname) {
        return chatService.getOnoMessages(nickname);
    }

    // 마감임박 경매 4개 조회
    @GetMapping("/auction/deadline")
    public ResponseDto<?> getDeadlineAuctions() {
        return auctionService.getDeadlineAuctions();
    }
}

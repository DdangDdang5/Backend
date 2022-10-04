package com.sparta.ddang.domain.auction.controller;

import com.sparta.ddang.domain.auction.dto.request.*;
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

    @RequestMapping(value = "/auction", method = RequestMethod.GET)
    public ResponseDto<?> getAllAuction(){
        return auctionService.getAllAuction();
    }

    @RequestMapping(value = "/auction/{auctionId}", method = RequestMethod.GET)
    public ResponseDto<?> getDetailAuction(@PathVariable Long auctionId,
                                           HttpServletRequest request){
        return auctionService.getDetailAuction(auctionId,request);
    }

    @RequestMapping(value = "/auction", method = RequestMethod.POST)
    public ResponseDto<?> createAuction(@RequestPart(value = "images",required = false) List<MultipartFile> multipartFile,
                                        @RequestPart(value = "auctionRequestDto")AuctionRequestDto auctionRequestDto,
                                        @RequestPart(value = "tags") AuctionTagsRequestDto auctionTagsRequestDto,
                                        HttpServletRequest request) throws IOException {
        return auctionService.createAuction(multipartFile, auctionRequestDto,auctionTagsRequestDto,request);
    }

    @RequestMapping(value = "/auction/{auctionId}", method = RequestMethod.PATCH)
    public ResponseDto<?> updateAuction(@RequestPart(value = "images",required = false) List<MultipartFile> multipartFile,
                                        @RequestPart(value = "auctionUpdateDto") AuctionUpdateRequestDto auctionUpdateRequestDto,
                                        @RequestPart(value = "tags") AuctionTagsRequestDto auctionTagsRequestDto,
                                        @PathVariable Long auctionId,
                                        HttpServletRequest request) throws IOException {
        return auctionService.updateAuction(multipartFile,auctionId,auctionUpdateRequestDto,auctionTagsRequestDto,request);
    }

    @RequestMapping(value = "/auction/{auctionId}", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteAuction( @PathVariable Long auctionId,
                                         HttpServletRequest request){
        return auctionService.deleteAuction(auctionId,request);
    }

    @RequestMapping(value = "/auction/category/{category}", method = RequestMethod.GET)
    public ResponseDto<?> findCategoryAuction(@PathVariable String category){
        return auctionService.findCategoryAuction(category);
    }

    @RequestMapping(value = "/auction/category/show", method = RequestMethod.GET)
    public ResponseDto<?> showCategoryAuction(){
        return auctionService.showCategoryAuction();
    }

    @RequestMapping(value = "/auction/region/{region}", method = RequestMethod.GET)
    public ResponseDto<?> findRegionAuction(@PathVariable String region){
        return auctionService.findRegionAuction(region);
    }

    @RequestMapping(value = "/auction/region/show", method = RequestMethod.GET)
    public ResponseDto<?> showRegionAuction(){
        return auctionService.showRegionAuction();
    }

    @RequestMapping(value = "/auction/category/{category}/region/{region}", method = RequestMethod.GET)
    public ResponseDto<?> findCategoryAndRegionAuction(@PathVariable String category,
                                                       @PathVariable String region){
        return auctionService.findCategoryAndRegionAuction(category,region);
    }

    @RequestMapping(value = "/auction/{auctionId}/join", method = RequestMethod.POST)
    public ResponseDto<?> joinAuction(@PathVariable Long auctionId,
                                         @RequestBody JoinPriceRequestDto joinPriceRequestDto,
                                         HttpServletRequest request){
        return auctionService.joinAuction(auctionId,joinPriceRequestDto,request);
    }

    @RequestMapping(value = "/member/mypage/participant",
            method = RequestMethod.GET)
    public ResponseDto<?> getAlljoinAuction(HttpServletRequest request){
        return auctionService.getAlljoinAuction(request);
    }

    @RequestMapping(value = "/auction/{auctionId}/favorite",
            method = RequestMethod.GET)
    public ResponseDto<?> addfavoriteAuction(@PathVariable Long auctionId,
                                      HttpServletRequest request){
        return auctionService.addfavoriteAuction(auctionId,request);
    }

    @RequestMapping(value = "/member/favorite", method = RequestMethod.GET)
    public ResponseDto<?> myfavoriteAuction(HttpServletRequest request){
        return auctionService.myfavoriteAuction(request);
    }

    @RequestMapping(value = "/member/mypage/myauction", method = RequestMethod.GET)
    public ResponseDto<?> getMyAuction(HttpServletRequest request){
        return auctionService.getMyAuction(request);
    }

    @RequestMapping(value = "/category/hit", method = RequestMethod.GET)
    public ResponseDto<?> getAllHitCategory(){
        return auctionService.getAllHitCategory();
    }

    @RequestMapping(value = "/region/hit", method = RequestMethod.GET)
    public ResponseDto<?> getAllHitRegion(){
        return auctionService.getAllHitRegion();
    }

    @RequestMapping(value = "/auction/search/{title}", method = RequestMethod.GET)
    public ResponseDto<?> getSearchTitle(@PathVariable String title,
                                         HttpServletRequest request){
        return auctionService.getSearchTitle(title,request);
    }

    @RequestMapping(value = "/auction/recent-search", method = RequestMethod.GET)
    public ResponseDto<?> getSearchRecent(HttpServletRequest request){
        return auctionService.getSearchRecent(request);
    }

    @RequestMapping(value = "/auction/popular-search", method = RequestMethod.GET)
    public ResponseDto<?> getSearchPopular(){
        return auctionService.getSearchPopular();
    }

    @RequestMapping(value = "/auction/{auctionId}/bidder", method = RequestMethod.GET)
    public ResponseDto<?> getBidder(@PathVariable Long auctionId){
        return auctionService.getBidder(auctionId);
    }

    @RequestMapping(value = "/auction/hit", method = RequestMethod.GET)
    public ResponseDto<?> getAuctionTop4(){
        return auctionService.getAuctionTop4();
    }

    @RequestMapping(value = "/auction/deadline",method = RequestMethod.GET)
    public ResponseDto<?> getDeadlineAuctions() {
        return auctionService.getDeadlineAuctions();
    }

    @RequestMapping(value = "/auction/new-release", method = RequestMethod.GET)
    public ResponseDto<?> getNewReleaseTop3(){
        return auctionService.getNewReleaseTop3();
    }

    @RequestMapping(value = "/ono/{nickname}", method = RequestMethod.GET)
    public ResponseDto<?> getOnoMessage(@PathVariable String nickname) {
        return chatService.getOnoMessages(nickname);
    }

    @RequestMapping(value = "/auction/{auctionId}/review", method=RequestMethod.POST)
    public ResponseDto<?> reviewAuction(@PathVariable Long auctionId,
                                        @RequestBody ReviewRequestDto reviewRequestDto,
                                        HttpServletRequest request) {
        return auctionService.reviewAuction(auctionId, reviewRequestDto, request);
    }

    @RequestMapping(value = "/auction/{auctionId}/done", method = RequestMethod.GET)
    public ResponseDto<?> doneAuction(@PathVariable Long auctionId, HttpServletRequest request) {
        return auctionService.doneAuction(auctionId, request);
    }
}

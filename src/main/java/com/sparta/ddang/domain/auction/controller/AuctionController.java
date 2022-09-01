package com.sparta.ddang.domain.auction.controller;

import com.sparta.ddang.domain.auction.dto.request.AuctionRequestDto;
import com.sparta.ddang.domain.auction.dto.request.AuctionUpdateRequestDto;
import com.sparta.ddang.domain.auction.service.AuctionService;
import com.sparta.ddang.domain.dto.ResponseDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService){

        this.auctionService= auctionService;

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

    // 경매 생성
    @RequestMapping(value = "/auction", method = RequestMethod.POST)
    public ResponseDto<?> createAuction(@RequestPart(value = "images",required = false) List<MultipartFile> multipartFile,
                                        @RequestPart(value = "auctionReqeustDto")AuctionRequestDto auctionRequestDto,
                                        HttpServletRequest request) throws IOException {
        return auctionService.createAuction(multipartFile, auctionRequestDto,request);

    }
    // 경매 수정
    @RequestMapping(value = "/auction/{auctionId}", method = RequestMethod.PATCH)
    public ResponseDto<?> updateAuction(@RequestPart(value = "images",required = false) List<MultipartFile> multipartFile,
                                        @RequestPart(value = "auctionUpdateDto") AuctionUpdateRequestDto auctionUpdateRequestDto,
                                        @PathVariable Long auctionId,
                                        HttpServletRequest request) throws IOException {


        return auctionService.updateAuction(multipartFile,auctionId,auctionUpdateRequestDto,request);

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

    // 경매 지역별 조회 --> 비로그인 회원도 조회가능하게 함.
    @RequestMapping(value = "/auction/region/{region}", method = RequestMethod.GET)
    public ResponseDto<?> findRegionAuction(@PathVariable String region){

        return auctionService.findRegionAuction(region);

    }

    // 경매 카테고리 & 지역별 조회 --> 비로그인 회원도 조회가능하게 함.
    // /auction/category/{category}/region/{region}
    @RequestMapping(value = "/auction/category/{category}/region/{region}", method = RequestMethod.GET)
    public ResponseDto<?> findCategoryAndRegionAuction(@PathVariable String category,
                                                       @PathVariable String region){

        return auctionService.findCategoryAndRegionAuction(category,region);
    }
}

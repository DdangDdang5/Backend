package com.sparta.ddang.domain.pagination.controller;

import com.sparta.ddang.domain.dto.ResponseDto;
import com.sparta.ddang.domain.pagination.service.PagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class PagingController {

    private final PagingService pagingService;

    @Autowired
    public PagingController(PagingService pagingService){

        this.pagingService = pagingService;

    }
    
    // 경매 전체 조회 페이지네이션
    @RequestMapping(value = "/pagination/auction", method = RequestMethod.GET)
    public ResponseDto<?> getAuctionPagenation(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc

    ) {
        page = page - 1;
        return pagingService.getAuctionPagenation(page, size,sortBy,isAsc);
    }
    
    // 경매 카테고리별 조회 페이지네이션
    @RequestMapping(value = "/pagination/auction/category/{category}", method = RequestMethod.GET)
    public ResponseDto<?> getCategoryPagenation(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @PathVariable String category

    ) {
        page = page - 1;
        return pagingService.getCategoryPagenation(category,page,size,sortBy,isAsc);
    }

    // 경매 지역별 조회 페이지네이션
    @RequestMapping(value = "/pagination/auction/region/{region}", method = RequestMethod.GET)
    public ResponseDto<?> getRegionPagenation(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @PathVariable String region

    ) {
        page = page - 1;
        return pagingService.getRegionPagenation(region,page,size,sortBy,isAsc);
    }

    // 경매 카테고리 및 지역별 조회 페이지네이션
    @RequestMapping(value = "/pagination/auction/category/{category}/region/{region}", method = RequestMethod.GET)
    public ResponseDto<?> getCateRegiPagenation(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @PathVariable String category,
            @PathVariable String region

    ) {
        page = page - 1;
        return pagingService.getCateRegiPagenation(category,region,page,size,sortBy,isAsc);
    }


    // 내가 참여중인 경매 조회 페이지네이션
    @RequestMapping(value = "/pagination/member/mypage/participant",
            method = RequestMethod.GET)
    public ResponseDto<?> getJoinPagenation(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            HttpServletRequest request

    ) {
        page = page - 1;
        return pagingService.getJoinPagenation(request,page,size,sortBy,isAsc);
    }


    // 내가 찜한 경매 조회 페이지네이션
    @RequestMapping(value = "/pagination/member/favorite", method = RequestMethod.GET)
    public ResponseDto<?> getMyFavoritePagenation(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            HttpServletRequest request

    ) {
        page = page - 1;
        return pagingService.getMyFavoritePagenation(request,page,size,sortBy,isAsc);
    }

    // 내가 시작한 경매 페이지네이션
    @RequestMapping(value = "/pagination/member/mypage/myauction", method = RequestMethod.GET)
    public ResponseDto<?> getMyAuctionPagenation(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            HttpServletRequest request

    ) {
        page = page - 1;
        return pagingService.getMyAuctionPagenation(request,page,size,sortBy,isAsc);
    }
    
    


}

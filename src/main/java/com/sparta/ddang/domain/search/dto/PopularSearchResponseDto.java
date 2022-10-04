package com.sparta.ddang.domain.search.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PopularSearchResponseDto {
    private String searchWord;
    private Long searchWordCnt;

    @Builder
    public PopularSearchResponseDto(String searchWord,Long searchWordCnt){
        this.searchWord = searchWord;
        this.searchWordCnt = searchWordCnt;
    }

}

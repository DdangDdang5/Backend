package com.sparta.ddang.domain.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class RecentSearchResponseDto {
    private String searchWord;
    private LocalDateTime searchTime;

    @Builder
    public RecentSearchResponseDto(String searchWord,LocalDateTime searchTime){
        this.searchWord = searchWord;
        this.searchTime = searchTime;
    }

}

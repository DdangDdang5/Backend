package com.sparta.ddang.domain.category.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryResponseDto {

    private Long categoryId;
    private String categoryName;
    private Long viewerCnt;


    @Builder
    public CategoryResponseDto(Long categoryId, String categoryName, Long viewerCnt){

        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.viewerCnt = viewerCnt;


    }

}

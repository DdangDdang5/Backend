package com.sparta.ddang.domain.category.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryOnlyResponseDto {

    private String categoryName;


    @Builder
    public CategoryOnlyResponseDto(String categoryName){

        this.categoryName = categoryName;

    }

}

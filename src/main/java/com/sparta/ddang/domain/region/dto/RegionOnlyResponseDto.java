package com.sparta.ddang.domain.region.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RegionOnlyResponseDto {
    private String region;

    @Builder
    public RegionOnlyResponseDto(String region){
        this.region = region;
    }

}

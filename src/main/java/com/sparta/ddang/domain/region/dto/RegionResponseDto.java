package com.sparta.ddang.domain.region.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RegionResponseDto {

    private Long regionId;
    private String regionName;
    private Long viewerCnt;


    @Builder
    public RegionResponseDto(Long regionId, String regionName, Long viewerCnt){

        this.regionId = regionId;
        this.regionName = regionName;
        this.viewerCnt = viewerCnt;


    }



}

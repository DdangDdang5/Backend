package com.sparta.ddang.domain.region.entity;

import com.sparta.ddang.util.Timestamped;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@Entity
public class Region extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "region",nullable = false)
    private String region;

    @Column(name = "viewerCnt",nullable = false)
    @ColumnDefault("0")
    private Long viewerCnt;


    public Region(){

    }

    public Region(String region, Long viewerCnt){

        this.region = region;
        this.viewerCnt = viewerCnt;

    }


    public void updateRegionCnt(String regi) {
        this.region = regi;
        this.viewerCnt += 1;

    }
}

package com.sparta.ddang.domain.category.entity;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category",nullable = false)
    private String category;

    @Column(name = "viewerCnt",nullable = false)
    @ColumnDefault("0")
    private Long viewerCnt;

    public Category(){}

    @Builder
    public Category(String category, Long viewerCnt){
        this.category = category;
        this.viewerCnt = viewerCnt;
    }

    public void updateCateCnt(String cate){
        this.category = cate;
        this.viewerCnt += 1;
    }

}

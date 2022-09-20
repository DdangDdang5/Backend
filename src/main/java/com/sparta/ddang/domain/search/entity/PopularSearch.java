package com.sparta.ddang.domain.search.entity;

import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@Entity
public class PopularSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String searchWord;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long searchWordCnt = 1L;

    public PopularSearch(){}

    public PopularSearch(String searchWord){

        this.searchWord = searchWord;

    }

    public void addSearchWordCnt(){

        this.searchWordCnt += 1;

    }

}

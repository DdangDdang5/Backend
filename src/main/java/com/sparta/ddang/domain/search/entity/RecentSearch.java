package com.sparta.ddang.domain.search.entity;

import com.sparta.ddang.util.Timestamped;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class RecentSearch extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String searchWord;

    public RecentSearch(){}

    public RecentSearch(Long memberId,String searchWord){
        this.memberId = memberId;
        this.searchWord = searchWord;
    }

    public void updateTime(LocalDateTime now){
        this.setModifiedAt(now);
    }

}

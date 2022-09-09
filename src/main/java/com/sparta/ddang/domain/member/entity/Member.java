package com.sparta.ddang.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.ddang.util.Timestamped;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "member")
@Getter
public class Member extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickName;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

//    @Column(nullable = false, unique = true)
//    private String phoneNum;

    @Column
    private String profileImgUrl;

    @Column
    private boolean isKakao;

//    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//    private List<Auction> auctionList;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void update(String nickName, String profileImgUrl) {

        this.nickName = nickName;
        this.profileImgUrl = profileImgUrl;

    }

    @Builder
    public Member(String email, String nickName, String password, String profileImgUrl, boolean isKakao) {
        //this.id = id;
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.profileImgUrl = profileImgUrl;
        this.isKakao = isKakao;
    }


    public Member() {}
}

package com.sparta.ddang.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.ddang.util.Authority;
import com.sparta.ddang.util.Timestamped;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.Set;

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

    @Column(nullable = false, unique = true)
    private String phoneNum;

    @Column
    private String profileImgUrl;

//    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//    private List<Auction> auctionList;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void update(String nickName, String phoneNum, String profileImgUrl) {

        this.nickName = nickName;
        this.phoneNum = phoneNum;
        this.profileImgUrl = profileImgUrl;

    }


    @Builder
    public Member(Long id, String email, String nickName, String password, String phoneNum, String profileImgUrl) {
        this.id = id;
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.phoneNum = phoneNum;
        this.profileImgUrl = profileImgUrl;
    }

    public Member() {}
}

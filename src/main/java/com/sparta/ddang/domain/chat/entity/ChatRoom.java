package com.sparta.ddang.domain.chat.entity;

import com.sparta.ddang.domain.auction.entity.Auction;
import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.util.Timestamped;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class ChatRoom extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private List<Member> memberList = new ArrayList<>();

    public ChatRoom(Member writer, Member applicant){
        this.roomName = writer.getNickName() + "님과 " + applicant.getNickName() + "님의 채팅방";
        this.memberList.add(writer);
        this.memberList.add(applicant);
    }

    public ChatRoom() {}
}

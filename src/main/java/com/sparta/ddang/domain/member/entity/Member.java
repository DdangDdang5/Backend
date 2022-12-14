package com.sparta.ddang.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.ddang.util.Timestamped;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Column
    private String profileImgUrl;

    @Column
    private boolean isKakao;

    @Column
    private int trustPoint;

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
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.profileImgUrl = profileImgUrl;
        this.isKakao = isKakao;
        this.trustPoint = 0;
    }

    public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.password);
    }

    public void updateTrustPoint(int trustPoint) {
        this.trustPoint += trustPoint;
    }

    public Member() {}
}

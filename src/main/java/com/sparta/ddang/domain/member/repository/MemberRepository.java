package com.sparta.ddang.domain.member.repository;

import com.sparta.ddang.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickName(String nickName);
//    Optional<Member> findById(Long memberId);
}

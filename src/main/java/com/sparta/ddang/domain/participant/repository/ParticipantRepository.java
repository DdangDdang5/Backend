package com.sparta.ddang.domain.participant.repository;

import com.sparta.ddang.domain.participant.entity.Participant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant,Long> {
    boolean existsByMemberIdAndAuctionId(Long memberId,Long auctionId);
    void deleteByMemberIdAndAuctionId(Long memberId,Long auctionId);
    Long countAllByAuctionId(Long auctionId);
    Long countAllByMemberId(Long memberId);
    List<Participant> findAllByMember_Id(Long memberId);
    Page<Participant> findAllByMember_Id(Long memberId, Pageable pageable);

}

package com.sparta.ddang.domain.participant.repository;

import com.sparta.ddang.domain.participant.entity.Participant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant,Long> {
    
    // 해당 회원이 해당 게시물에 등록됬는지 확인
    boolean existsByMemberIdAndAuctionId(Long memberId,Long auctionId);

    // 해당 회원이 해당 게시물에 등록되어 있으면 삭제
    void deleteByMemberIdAndAuctionId(Long memberId,Long auctionId);

    // Participant테이블에 경매 게시글이 몇개 있는지만 확인하면 몇명에
    // 해당 경매 게시글에 참여했는지 알수 있고 따라서 Participant 컬럼 갯수 반환하면 됨
    Long countAllByAuctionId(Long auctionId);
    Long countAllByMemberId(Long memberId);


    List<Participant> findAllByMember_Id(Long memberId);
    Page<Participant> findAllByMember_Id(Long memberId, Pageable pageable);

}

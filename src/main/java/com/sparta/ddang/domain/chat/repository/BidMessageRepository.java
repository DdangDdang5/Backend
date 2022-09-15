package com.sparta.ddang.domain.chat.repository;

import com.sparta.ddang.domain.chat.entity.BidMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidMessageRepository extends JpaRepository<BidMessage,Long> {

}

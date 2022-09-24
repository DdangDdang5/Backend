package com.sparta.ddang.domain.chat.repository;

import com.sparta.ddang.domain.chat.entity.OnoChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnoChatMessageRepository extends JpaRepository<OnoChatMessage,Long> {

    //boolean existsByRoomIdAndNickName(String roomId, String nickName);

    //List<OnoChatMessage> findAllByBidderAndSeller(String nickName);

    boolean existsByRoomId(String roomId);

    //void deleteAllByRoomIdAndNickName(String roomId, String nickName);

    void deleteAllByRoomId(String roomId);

    //List<OnoChatMessage> findAllByNickName(String nickName);
}
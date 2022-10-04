package com.sparta.ddang.domain.chat.repository;

import com.sparta.ddang.domain.chat.entity.OnoChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OnoChatMessageRepository extends JpaRepository<OnoChatMessage,Long> {
    boolean existsByRoomId(String roomId);
    void deleteAllByRoomId(String roomId);
    List<OnoChatMessage> findAllByOrderByLastMessageTimeDesc();

}
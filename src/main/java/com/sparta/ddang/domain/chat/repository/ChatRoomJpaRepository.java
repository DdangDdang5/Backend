package com.sparta.ddang.domain.chat.repository;

import com.sparta.ddang.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom,Long> {
    List<ChatRoom> findAllByOrderByCreatedAtDesc();

    ChatRoom findByRoomId(String roomId);

    List<ChatRoom> findAllByRoomName(String roomName);
    Optional<ChatRoom> findByRoomName(String roomName);
}




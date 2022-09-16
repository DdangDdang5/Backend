package com.sparta.ddang.domain.chat.repository;

import com.sparta.ddang.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {

    List<ChatMessage> findAllByRoomId(String roomId);

    //List<ChatMessage> findByNickNameAndRoomNameContaining(String nickname,String ono);

    //List<ChatMessage> findAllByNickNameAndRoomNameContaining(String nickname,String ono);
    List<ChatMessage> findAllByNickNameAndRoomNameContainingOrderByCreatedAtDesc(String nickname,String ono);

}

package com.sparta.ddang.domain.chat.service;

import com.sparta.ddang.domain.chat.dto.ChatMessageDto;
import com.sparta.ddang.domain.chat.entity.BidMessage;
import com.sparta.ddang.domain.chat.entity.ChatMessage;
import com.sparta.ddang.domain.chat.repository.ChatMessageJpaRepository;
import com.sparta.ddang.domain.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private static final String CHAT_MESSAGE = "CHAT_MESSAGE"; // 채팅룸에 메세지들을 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
    private final ChatMessageJpaRepository chatMessageJpaRepository;
    private final RedisTemplate<String, Object> redisTemplate; // redisTemplate 사용
    private final StringRedisTemplate stringRedisTemplate; // StringRedisTemplate 사용
    private HashOperations<String, String, String> hashOpsEnterInfo; // Redis 의 Hashes 사용

    private HashOperations<String, String, List<ChatMessageDto>> opsHashChatMessageDto; // Redis 의 Hashes 사용
    private HashOperations<String, String, List<ChatMessage>> opsHashChatMessage; // Redis 의 Hashes 사용
    private HashOperations<String, String, List<BidMessage>> opsHashChatBidMessage; // Redis 의 Hashes 사용
    private ValueOperations<String, String> valueOps; // Redis 의 String 구조 사용

    //초기화
    @PostConstruct
    private void init() {
        opsHashChatMessage = redisTemplate.opsForHash();
        opsHashChatBidMessage = redisTemplate.opsForHash();
        hashOpsEnterInfo = redisTemplate.opsForHash();
        valueOps = stringRedisTemplate.opsForValue();
    }

    //redis 에 메세지 저장하기
    @Transactional
    public ChatMessage save(ChatMessage chatMessage) {
        //chatMessageDto 를 redis 에 저장하기 위하여 직렬화 한다.
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));

        String roomId = chatMessage.getRoomId();

        //redis에 저장되어있는 리스트를 가져와, 새로 받아온 chatmessageDto를 더하여 다시 저장한다.
        List<ChatMessage> chatMessageList = opsHashChatMessage.get(CHAT_MESSAGE, roomId);

        //가져온 List가 null일때 새로운 리스트를 만든다 == 처음에 메세지를 저장할경우 리스트가 없기때문에.
        if (chatMessageList == null) {
            chatMessageList = new ArrayList<>();
        }
        chatMessageList.add(chatMessage);
        //redis 의 hashes 자료구조
        //key : CHAT_MESSAGE , filed : roomId, value : chatMessageList
        opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageList);
        System.out.println();
        redisTemplate.expire(CHAT_MESSAGE,24, TimeUnit.HOURS);

        System.out.println("===========================ChatMessage save 시간 :"+ chatMessage.getCreatedAt());

        return chatMessage;
    }

    //redis 에 메세지 저장하기
    @Transactional
    public BidMessage saveBid(BidMessage bidMessage) {
        //chatMessageDto 를 redis 에 저장하기 위하여 직렬화 한다.
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(BidMessage.class));

        String roomId = bidMessage.getRoomId();

        //redis에 저장되어있는 리스트를 가져와, 새로 받아온 chatmessageDto를 더하여 다시 저장한다.
        List<BidMessage> bidMessageList = opsHashChatBidMessage.get(CHAT_MESSAGE, roomId);

        //가져온 List가 null일때 새로운 리스트를 만든다 == 처음에 메세지를 저장할경우 리스트가 없기때문에.
        if (bidMessageList == null) {
            bidMessageList = new ArrayList<>();
        }
        bidMessageList.add(bidMessage);
        //redis 의 hashes 자료구조
        //key : CHAT_MESSAGE , filed : roomId, value : chatMessageList
        opsHashChatBidMessage.put(CHAT_MESSAGE, roomId, bidMessageList);
        System.out.println();
        redisTemplate.expire(CHAT_MESSAGE,24, TimeUnit.HOURS);

        System.out.println("===========================ChatMessage save 시간 :"+ bidMessage.getCreatedAt());

        return bidMessage;
    }

    //채팅 리스트 가져오기
    @Transactional
    public ResponseDto<?> findAllMessage(String roomId) {
        List<ChatMessage> chatMessageList = new ArrayList<>();

        if (opsHashChatBidMessage.size(CHAT_MESSAGE) > 0) {
            // 여기로 접근하고 있었음. 채팅시 캐시에 키값으로 CHAT_MESSAGE가 저장되어 있었으니까
            //System.out.println("redis 1"+opsHashChatBidMessage.get(CHAT_MESSAGE, roomId).get(0).getCreatedAt());

            return ResponseDto.success (opsHashChatBidMessage.get(CHAT_MESSAGE, roomId));

        }

        //chatMessage 리스트를 불러올때, 리스트의 사이즈가 0보다 크면 redis 정보를 가져온다
        //redis 에서 가져온 리스트의 사이즈가 0보다 크다 == 저장된 정보가 있다.
        if (opsHashChatMessage.size(CHAT_MESSAGE) > 0) {
            // 여기로 접근하고 있었음. 채팅시 캐시에 키값으로 CHAT_MESSAGE가 저장되어 있었으니까
            System.out.println("redis 1"+opsHashChatMessage.get(CHAT_MESSAGE, roomId).get(0).getCreatedAt());

            return ResponseDto.success (opsHashChatMessage.get(CHAT_MESSAGE, roomId));

        } else { // redis 에서 가져온 메세지 리스트의 사이즈가 0보다 작다 == redis에 정보가 없다.
                 // 채팅할때 redis에 저장되지 않았을 경우

            List<ChatMessage> chatMessages = chatMessageJpaRepository.findAllByRoomId(roomId);

            System.out.println("redis 2"+chatMessages.get(0).getCreatedAt());

            for (ChatMessage chatMessage : chatMessages) {
                // 시간이 안넣어 짐.
                LocalDateTime createdAt = chatMessage.getCreatedAt();

                System.out.println("================================= redis저장시간"+createdAt);

                String createdAtString = createdAt.format(DateTimeFormatter.ofPattern("dd,MM,yyyy,HH,mm,ss", Locale.KOREA));

                ChatMessage chatMessage1 = new ChatMessage(chatMessage);

                chatMessageList.add(chatMessage1);
            }
            //redis에 정보가 없으니, 다음부터 조회할때는 redis를 사용하기 위하여 넣어준다.
            opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageList);

            return ResponseDto.success(chatMessageList);
        }


    }

}


////chatMessage 리스트를 불러올때, 리스트의 사이즈가 0보다 크면 redis 정보를 가져온다
//        //redis 에서 가져온 리스트의 사이즈가 0보다 크다 == 저장된 정보가 있다.
//        if (opsHashChatMessage.size(CHAT_MESSAGE) > 0) {
//            // 여기로 접근하고 있었음. 채팅시 캐시에 키값으로 CHAT_MESSAGE가 저장되어 있었으니까
//            System.out.println("redis 1"+opsHashChatMessage.get(CHAT_MESSAGE, roomId).get(0).getCreatedAt());
//
//            return ResponseDto.success (opsHashChatMessage.get(CHAT_MESSAGE, roomId));
//
//        } else { // redis 에서 가져온 메세지 리스트의 사이즈가 0보다 작다 == redis에 정보가 없다.
//                 // 채팅할때 redis에 저장되지 않았을 경우
//
//            List<ChatMessage> chatMessages = chatMessageJpaRepository.findAllByRoomId(roomId);
//
//            System.out.println("redis 2"+chatMessages.get(0).getCreatedAt());
//
//            for (ChatMessage chatMessage : chatMessages) {
//                // 시간이 안넣어 짐.
//                LocalDateTime createdAt = chatMessage.getCreatedAt();
//
//                System.out.println("================================= redis저장시간"+createdAt);
//
//                String createdAtString = createdAt.format(DateTimeFormatter.ofPattern("dd,MM,yyyy,HH,mm,ss", Locale.KOREA));
//
//                ChatMessage chatMessage1 = new ChatMessage(chatMessage);
//
//                chatMessageList.add(chatMessage1);
//            }
//            //redis에 정보가 없으니, 다음부터 조회할때는 redis를 사용하기 위하여 넣어준다.
//            opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageList);
//
//            return ResponseDto.success(chatMessageList);
//        }
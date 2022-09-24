package com.sparta.ddang.util;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class TimestampedChat {

    // Java 8 date/time type java.time.LocalDateTime not supported by default
    // LocalDataTime을 역직렬화하지 못해서 생기는 문제입니다.
    // 추가적으로 만약 캐시로 사용할 객체에 LocalDateTime 타입의 값이
    // 존재한다면 위처럼 @JsonSerialize, @JsonDeserialize 어노테이션을 기입해줘야 합니다.
    // 그렇지 않으면 오류가 발생합니다.

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @LastModifiedDate
    private LocalDateTime modifiedAt;

}

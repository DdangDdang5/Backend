package com.sparta.ddang.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Timestamped {


//    @JsonFormat(pattern = "yyyy-MM-dd")
    @CreatedDate
    private LocalDate createdAt;

//    @JsonFormat(pattern = "yyyy-MM-dd")
    @LastModifiedDate
    private LocalDate modifiedAt;

}

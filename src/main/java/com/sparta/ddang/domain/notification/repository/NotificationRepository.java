package com.sparta.ddang.domain.notification.repository;

import com.sparta.ddang.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}

package com.codec.system.application.service;

import codec.common.Response;
import com.codec.system.application.command.request.notificationConfig.NotificationReadRequest;
import com.codec.system.application.command.response.notificationConfig.NotificationUserResponse;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface NotificationUserService {
  RestCodecSystemApplicationPage<NotificationUserResponse> getMyNotifications(
    String userId, LocalDate from, LocalDate to, Pageable pageable);
  Response<Void> markAsRead(NotificationReadRequest request, String userId);

  Response<Long> countUnread(String userId);
}

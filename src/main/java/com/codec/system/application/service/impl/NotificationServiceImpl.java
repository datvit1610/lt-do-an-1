package com.codec.system.application.service.impl;

import com.codec.system.application.command.response.notification.NotificationListResponse;
import com.codec.system.application.command.response.notification.NotificationResponse;
import com.codec.system.application.service.NotificationService;
import com.codec.system.domain.entity.NotificationEntity;
import com.codec.system.domain.repository.NotificationRepository;
import com.codec.system.pagination.domain.CodecSystemApplicationPage;
import com.codec.system.pagination.domain.CodecSystemApplicationPageable;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  NotificationRepository notificationRepository;

  @Override
  public NotificationListResponse getNotifications(String userId, Pageable pageable) {
    Page<NotificationEntity> page =
      notificationRepository.findByUserIdAndDeletedFalseOrderByCreatedDateDesc(userId, pageable);

    List<NotificationResponse> responses = page.stream()
      .map(NotificationResponse::new)
      .toList();

    CodecSystemApplicationPageable codecPageable =
      new CodecSystemApplicationPageable(pageable.getPageNumber(), pageable.getPageSize());

    RestCodecSystemApplicationPage<NotificationResponse> rest = RestCodecSystemApplicationPage.from(
      CodecSystemApplicationPage.of(responses, codecPageable, page.getTotalElements()), d -> d);

    long unreadCount = notificationRepository.countUnread(userId);

    return new NotificationListResponse(rest, unreadCount);
  }

  @Override
  @Transactional
  public void markOneRead(String id) {
    notificationRepository.markOneRead(id);
  }

  @Override
  @Transactional
  public void markAllRead(String userId) {
    notificationRepository.markAllRead(userId);
  }
}

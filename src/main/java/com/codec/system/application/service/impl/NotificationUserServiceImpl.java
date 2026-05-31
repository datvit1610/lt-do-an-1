package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.application.command.request.notificationConfig.NotificationReadRequest;
import com.codec.system.application.command.response.notificationConfig.NotificationLogResponse;
import com.codec.system.application.command.response.notificationConfig.NotificationUserResponse;
import com.codec.system.application.service.NotificationUserService;
import com.codec.system.domain.entity.NotificationLogEntity;
import com.codec.system.domain.entity.NotificationUserEntity;
import com.codec.system.domain.repository.NotificationUserRepository;
import com.codec.system.pagination.domain.CodecSystemApplicationPage;
import com.codec.system.pagination.domain.CodecSystemApplicationPageable;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationUserServiceImpl implements NotificationUserService {

  private final NotificationUserRepository notificationUserRepository;

  // Danh sách thông báo theo user đăng nhập
  @Override
  public RestCodecSystemApplicationPage<NotificationUserResponse> getMyNotifications(
    String userId, LocalDate from, LocalDate to, Pageable pageable) {
    // Validate khoảng thời gian không quá 1 năm
    if (from != null && to != null) {
      if (from.isAfter(to)) {
        throw new RuntimeException("Ngày bắt đầu không được lớn hơn ngày kết thúc");
      }
      if (from.plusYears(1).isBefore(to)) {
        throw new RuntimeException("Khoảng thời gian tìm kiếm không được vượt quá 1 năm");
      }
    }
    LocalDateTime fromDateTime = (from != null) ? from.atStartOfDay() : null;
    LocalDateTime toDateTime   = (to   != null) ? to.atTime(23, 59, 59) : null;
    Page<Tuple> result = notificationUserRepository
      .findByUserId(userId, fromDateTime, toDateTime, pageable);
    List<NotificationUserResponse> list = result.getContent().stream().map(NotificationUserResponse::new).toList();
    long currentCount = result.getTotalElements();
    CodecSystemApplicationPageable codecPageable = new CodecSystemApplicationPageable(pageable.getPageNumber(), pageable.getPageSize());
    return RestCodecSystemApplicationPage
      .from(CodecSystemApplicationPage
        .of(list, codecPageable, currentCount), notify-> notify);
  }

  // Đánh dấu đã đọc
  @Override
  @Transactional
  public Response<Void> markAsRead(NotificationReadRequest request, String userId) {

    if (Boolean.TRUE.equals(request.getIsReadAll())) {
      // Đọc hết tất cả thông báo chưa đọc của user
      List<NotificationUserEntity> unreadList = notificationUserRepository
        .findByUserIdAndIsRead(userId, false);

      unreadList.forEach(entity -> {
        entity.setIsRead(true);
        entity.setReadAt(LocalDateTime.now());
      });

      notificationUserRepository.saveAll(unreadList);

    } else {
      // Đọc theo id truyền vào
      NotificationUserEntity entity = notificationUserRepository
        .findFirstByNotificationLogIdAndUserId(request.getNotificationLogId(), userId)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));

      if (!entity.getIsRead()) {
        entity.setIsRead(true);
        entity.setReadAt(LocalDateTime.now());
        notificationUserRepository.save(entity);
      }
    }

    return Response.<Void>of(null).success("Đã đọc thông báo", 201);
  }

  @Override
  public Response<Long> countUnread(String userId) {
    long count = notificationUserRepository.countByUserIdAndIsRead(userId, false);
    return Response.of(count).success("OK", 200);
  }
}

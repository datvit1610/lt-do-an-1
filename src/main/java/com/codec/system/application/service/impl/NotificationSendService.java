package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.domain.entity.NotificationLogEntity;
import com.codec.system.domain.entity.NotificationUserEntity;
import com.codec.system.domain.entity.RolePermissionEntity;
import com.codec.system.domain.repository.*;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSendService {

  private final NotificationLogRepository notificationLogRepository;
  private final RolePermissionRepository rolePermissionRepository;
  private final UserRepository userRepository;
  private final UserDeviceRepository userDeviceRepository;
  private final FirebaseMessaging firebaseMessaging;
  private final NotificationUserRepository notificationUserRepository;

  // Gọi bởi NotificationScheduler theo giờ cấu hình
  @Transactional
  public void sendNotification() {
    log.info("Bắt đầu gửi thông báo Firebase...");

    NotificationLogEntity pending = notificationLogRepository.findFirstBySentAtIsNullOrderByIdDesc();
    if (pending == null) {
      log.info("Không có thông báo nào cần gửi.");
      return;
    }

    // Lấy roleId có quyền notify-v
    List<String> roleIds = rolePermissionRepository
      .findByPermissionName("notify-v")
      .stream()
      .map(RolePermissionEntity::getRoleId)
      .collect(Collectors.toList());

    if (roleIds.isEmpty()) {
      log.info("Không có role nào có quyền notify-v.");
      markSent(pending, 0, 0);
      return;
    }

    // Lấy userId active thuộc các role đó
    List<String> userIds = userRepository.findActiveUserIdsByRoleIds(roleIds);
    if (userIds.isEmpty()) {
      log.info("Không có user nào active.");
      markSent(pending, 0, 0);
      return;
    }

    // Lấy FCM token active
    List<String> tokens = userDeviceRepository.findActiveTokensByUserIds(userIds);
    if (tokens.isEmpty()) {
      log.info("Không có FCM token nào.");
      markSent(pending, 0, 0);
      return;
    }

    // Gửi từng token
    int successCount = 0;
    int failCount    = 0;
    for (String token : tokens) {
      try {
        Message message = Message.builder()
          .setToken(token)
          .setNotification(Notification.builder()
            .setTitle("Cảnh báo hết hạn sản phẩm")
            .setBody(pending.getContent())
            .build())
          .putData("type", "NEAR_EXPIRY")
          .putData("nearExpiryCount", String.valueOf(pending.getNearExpiryCount()))
          .build();
        firebaseMessaging.send(message);
        successCount++;
      } catch (FirebaseMessagingException e) {
        log.error("Gửi FCM thất bại token {}: {}", token, e.getMessage());
        failCount++;
      }
    }

    markSent(pending, successCount, failCount);
    log.info("Gửi xong: {} thành công, {} thất bại", successCount, failCount);

    // Tạo bản ghi isRead = false cho từng user nhận được thông báo
    List<NotificationUserEntity> notificationUsers = userIds.stream()
      .map(userId -> {
        NotificationUserEntity nu = new NotificationUserEntity();
        nu.setNotificationLogId(pending.getId());
        nu.setUserId(userId);
        nu.setIsRead(false);
        return nu;
      })
      .collect(Collectors.toList());
    notificationUserRepository.saveAll(notificationUsers);
  }

  private void markSent(NotificationLogEntity log, int success, int fail) {
    log.setSentAt(LocalDateTime.now());
    log.setSuccessCount(success);
    log.setFailCount(fail);
    notificationLogRepository.save(log);
  }


  // Thêm vào NotificationSendService
  public Response<String> testSend(String fcmToken, String title, String body) {
    try {
      Message message = Message.builder()
        .setToken(fcmToken)
        .setNotification(Notification.builder()
          .setTitle(title)
          .setBody(body)
          .build())
        .build();
      String response = firebaseMessaging.send(message);
      return Response.of("Gửi thành công: " + response).success("OK", 200);
    } catch (FirebaseMessagingException e) {
      return Response.fail("Gửi thất bại: " + e.getMessage(), 400);
    }
  }
}

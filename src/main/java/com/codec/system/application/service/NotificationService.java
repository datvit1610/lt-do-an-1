package com.codec.system.application.service;

import com.codec.system.application.command.response.notification.NotificationListResponse;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

  /**
   * Danh sách thông báo của một người dùng (phân trang) kèm tổng số chưa đọc.
   */
  NotificationListResponse getNotifications(String userId, Pageable pageable);

  /**
   * Đánh dấu một thông báo đã đọc.
   */
  void markOneRead(String id);

  /**
   * Đánh dấu tất cả thông báo của một người dùng đã đọc.
   */
  void markAllRead(String userId);
}

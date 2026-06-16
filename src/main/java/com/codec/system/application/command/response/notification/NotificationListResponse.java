package com.codec.system.application.command.response.notification;

import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationListResponse {
  // Danh sách thông báo (phân trang, sắp xếp theo thời gian tạo giảm dần)
  private RestCodecSystemApplicationPage<NotificationResponse> items;
  // Tổng số thông báo chưa đọc
  private long unreadCount;
}

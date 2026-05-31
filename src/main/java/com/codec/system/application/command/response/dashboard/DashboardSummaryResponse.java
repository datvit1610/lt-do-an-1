package com.codec.system.application.command.response.dashboard;

import lombok.Data;

// Response 1 - 4 thẻ tổng quan
@Data
public class DashboardSummaryResponse {
  private Long totalProducts;       // Tổng sản phẩm trong kho
  private Long expiredCount;     // Hết hạn
  private Long nearExpiryCount;     // Sắp hết hạn
  private Long importedTodayCount;  // Phiếu nhập hôm nay
  private Long sentNotificationCount; // Số lượng thông báo đã gửi
}

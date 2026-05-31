package com.codec.system.application.command.response.dashboard;

import lombok.Data;

// Response 2 - Biểu đồ theo nhóm
@Data
public class DashboardGroupResponse {
  private String productGroup;      // Tên nhóm
  private Long total;               // Tổng sản phẩm
  private Long nearExpiryCount;     // Gần hết hạn
  private Long validCount;          // Còn hạn
  private Double nearExpiryPercent; // % gần hết hạn
}

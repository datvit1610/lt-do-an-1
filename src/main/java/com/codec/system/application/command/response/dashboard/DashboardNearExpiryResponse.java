package com.codec.system.application.command.response.dashboard;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

// Response 3 - Danh sách sản phẩm gần hết hạn
@Data
public class DashboardNearExpiryResponse {
  private String productName;
  private String productGroup;
  private String storageLocation;

  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate expiryDate;
  //cột số ngày còn hạn
  private String daysUntilExpiry;
}

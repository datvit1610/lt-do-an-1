package com.codec.system.application.command.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DeviceTypeLoanResponse {

  /** Danh sách từng loại thiết bị */
  private List<DeviceTypeItem> data;

  /** Tổng lượt mượn */
  private Long total;

  /** Loại thiết bị được mượn nhiều nhất */
  private String mostPopularType;

  @Data
  @AllArgsConstructor
  public static class DeviceTypeItem {

    /** Tên loại thiết bị (device_type) */
    private String deviceType;

    /** Tổng lượt mượn của loại này */
    private Long totalLoans;

    /** Tỷ lệ phần trăm (làm tròn 1 chữ số thập phân) */
    private Double percentage;
  }
}

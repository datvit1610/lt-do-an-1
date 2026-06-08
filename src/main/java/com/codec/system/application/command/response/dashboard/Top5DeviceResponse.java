package com.codec.system.application.command.response.dashboard;

import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Một item trong danh sách top 5 thiết bị mượn nhiều nhất.
 */
@Data
@AllArgsConstructor
public class Top5DeviceResponse {

  /** ID thiết bị */
  private String deviceId;

  /** Mã thiết bị */
  private String deviceCode;

  /** Tên thiết bị */
  private String deviceName;

  /** Tổng lượt mượn trong khoảng thời gian */
  private Long totalLoans;

  public Top5DeviceResponse(Tuple tuple) {
    this.deviceId = tuple.get("deviceId", String.class);
    this.deviceCode = tuple.get("deviceCode", String.class);
    this.deviceName = tuple.get("deviceName", String.class);
    this.totalLoans = tuple.get("totalLoans", Long.class);
  }
}


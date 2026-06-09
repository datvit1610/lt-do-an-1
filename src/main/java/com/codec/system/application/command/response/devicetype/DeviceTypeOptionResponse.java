package com.codec.system.application.command.response.devicetype;

import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dữ liệu rút gọn của loại thiết bị dùng cho dropdown chọn loại (vd: lúc tạo thiết bị).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTypeOptionResponse {
  private String id;
  private String deviceType;

  public DeviceTypeOptionResponse(Tuple tuple) {
    this.id = tuple.get("id", String.class);
    this.deviceType = tuple.get("deviceType", String.class);
  }
}

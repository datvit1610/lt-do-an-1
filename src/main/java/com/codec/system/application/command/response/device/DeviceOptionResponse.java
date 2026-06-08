package com.codec.system.application.command.response.device;

import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dữ liệu rút gọn của thiết bị dùng cho dropdown chọn thiết bị (vd: lúc tạo phiếu mượn).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceOptionResponse {
  private String id;
  private String name;

  public DeviceOptionResponse(Tuple tuple) {
    this.id = tuple.get("id", String.class);
    this.name = tuple.get("name", String.class);
  }
}

package com.codec.system.application.command.request.devicetype;

import lombok.Data;

@Data
public class CreateDeviceTypeRequest {
  // Tên loại thiết bị
  private String deviceType;
}

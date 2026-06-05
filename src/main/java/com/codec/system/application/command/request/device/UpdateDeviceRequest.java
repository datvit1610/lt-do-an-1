package com.codec.system.application.command.request.device;

import lombok.Data;

@Data
public class UpdateDeviceRequest {
  // Tên thiết bị
  private String name;

  // Mã thiết bị
  private String deviceCode;

  // Loại thiết bị
  private String deviceType;

  // Trạng thái: 1: hoạt động, 0: ngưng
  private Integer status;

  // Vị trí thiết bị
  private String location;

  // Số lượng
  private Integer quantity;

  // Mô tả thiết bị
  private String description;
}

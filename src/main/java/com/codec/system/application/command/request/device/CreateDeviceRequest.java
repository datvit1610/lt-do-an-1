package com.codec.system.application.command.request.device;

import lombok.Data;

@Data
public class CreateDeviceRequest {
  // Tên thiết bị
  private String name;

  // Số serial thiết bị
  private String serialNumber;

  // Loại thiết bị
  private String deviceType;

  // Trạng thái: 1: hoạt động, 0: ngưng
  private Integer status;

  // Vị trí thiết bị
  private String location;

  // Id người được gán
  private String assignedUserId;

  // Mô tả thiết bị
  private String description;
}

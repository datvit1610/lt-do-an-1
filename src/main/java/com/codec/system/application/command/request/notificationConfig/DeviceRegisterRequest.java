package com.codec.system.application.command.request.notificationConfig;

import lombok.Data;

@Data
public class DeviceRegisterRequest {
  private String fcmToken;
  private String platform; // Android / iOS
}

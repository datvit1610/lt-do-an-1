package com.codec.system.application.command.request.notificationConfig;

import lombok.Data;

@Data
public class TestNotificationRequest {
  private String fcmToken;
  private String title;
  private String body;
}

package com.codec.system.application.command.response.notificationConfig;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationLogResponse {
  private String id;
  private String content;
  private Integer nearExpiryCount;

  private LocalDateTime sentAt;

  private Integer successCount;
  private Integer failCount;
}

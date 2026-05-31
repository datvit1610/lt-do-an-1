package com.codec.system.application.command.request.notificationConfig;

import lombok.Data;

@Data
public class NotificationReadRequest {
  private String notificationLogId;
  private Boolean isReadAll;
}

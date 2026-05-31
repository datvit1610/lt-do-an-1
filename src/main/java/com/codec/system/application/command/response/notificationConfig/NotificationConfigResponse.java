package com.codec.system.application.command.response.notificationConfig;

import lombok.Data;

@Data
public class NotificationConfigResponse {
  private Integer expiredDayNotify;
  private Integer notifyHour;
  private Integer notifyMinute;

}

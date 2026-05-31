package com.codec.system.application.command.request.notificationConfig;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationConfigRequest {
  @NotNull(message = "Ngưỡng thông báo không được để trống")
  @Min(value = 1, message = "Ngưỡng thông báo phải lớn hơn 0")
  private Integer expiredDayNotify;

  @Min(value = 0) @Max(value = 23)
  private Integer notifyHour = 6;

  @Min(value = 0) @Max(value = 59)
  private Integer notifyMinute = 0;
}

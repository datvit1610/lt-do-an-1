package com.codec.system.config;

import com.codec.system.application.service.impl.NotificationSendService;
import com.codec.system.domain.entity.NotificationConfigEntity;
import com.codec.system.domain.repository.NotificationConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

  private final NotificationConfigRepository notificationConfigRepository;
  private final NotificationSendService notificationSendService;
  private final TaskScheduler taskScheduler;

  private ScheduledFuture<?> scheduledTask;

  @PostConstruct
  public void init() {
    scheduleNotification();
  }

  // Gọi lại khi admin cập nhật giờ trong config
  public void reschedule() {
    if (scheduledTask != null) {
      scheduledTask.cancel(false);
    }
    scheduleNotification();
  }

  private void scheduleNotification() {
    NotificationConfigEntity config = notificationConfigRepository.findFirstByOrderByIdAsc();
    int hour   = (config != null && config.getNotifyHour()   != null) ? config.getNotifyHour()   : 6;
    int minute = (config != null && config.getNotifyMinute() != null) ? config.getNotifyMinute() : 0;

    String cron = String.format("0 %d %d * * *", minute, hour);
    log.info("Đặt lịch bắn thông báo lúc {}h{}m (cron: {})", hour, minute, cron);

    scheduledTask = taskScheduler.schedule(
      notificationSendService::sendNotification,
      new CronTrigger(cron)
    );
  }
}

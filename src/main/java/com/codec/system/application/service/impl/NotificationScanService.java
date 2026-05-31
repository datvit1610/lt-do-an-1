package com.codec.system.application.service.impl;

import com.codec.system.domain.entity.NotificationConfigEntity;
import com.codec.system.domain.entity.NotificationLogEntity;
import com.codec.system.domain.repository.NotificationConfigRepository;
import com.codec.system.domain.repository.NotificationLogRepository;
import com.codec.system.domain.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationScanService {

  private final WarehouseRepository warehouseRepository;
  private final NotificationConfigRepository notificationConfigRepository;
  private final NotificationLogRepository notificationLogRepository;

  // Quét DB lúc 23h30 mỗi đêm
  @Scheduled(cron = "0 30 10 * * *") //set thì 17h sẽ chạy, chậm 7 tiếng
  @Transactional
  public void scanNearExpiry() {
    log.info("Bắt đầu quét sản phẩm sắp hết hạn...");

    NotificationConfigEntity config = notificationConfigRepository.findFirstByOrderByIdAsc();
    int days = (config != null && config.getExpiredDayNotify() != null)
      ? config.getExpiredDayNotify() : 30;

    LocalDate today         = LocalDate.now();
    LocalDate thresholdDate = today.plusDays(days);

    long count = warehouseRepository.countNearExpiry(today, thresholdDate);
    log.info("Quét xong: {} sản phẩm sắp hết hạn", count);

    if (count > 0) {
      NotificationLogEntity log = new NotificationLogEntity();
      log.setNearExpiryCount((int) count);
      log.setContent("Trong kho đang có " + count + " sản phẩm gần hết hạn. Vui lòng sử dụng trước ngày hết hạn.");
      log.setSentAt(null); // chưa gửi, chờ job 6h
      notificationLogRepository.save(log);
    }
  }
}

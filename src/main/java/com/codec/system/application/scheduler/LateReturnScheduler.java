package com.codec.system.application.scheduler;

import com.codec.system.domain.entity.ClassPeriodEntity;
import com.codec.system.domain.entity.DeviceEntity;
import com.codec.system.domain.entity.LoanEntity;
import com.codec.system.domain.entity.NotificationEntity;
import com.codec.system.domain.entity.UserEntity;
import com.codec.system.domain.repository.ClassPeriodRepository;
import com.codec.system.domain.repository.DeviceRepository;
import com.codec.system.domain.repository.LoanRepository;
import com.codec.system.domain.repository.NotificationRepository;
import com.codec.system.domain.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Quét phiếu mượn định kỳ (10 phút/lần) để phát hiện trả chậm thiết bị.
 * - Lấy các phiếu đang mượn (status = 1) có borrow_date trong ngày hôm nay.
 * - Tính giờ kết thúc (end_time) của tiết trả (return_period) từ bảng class_periods.
 * - Nếu giờ hiện tại > end_time => trả chậm: cập nhật status = 3, late_minutes,
 *   và sinh thông báo LATE_RETURN (tránh insert trùng theo ref_id = loan_id).
 */
@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LateReturnScheduler {

  static final ZoneId ZONE = ZoneId.of("GMT+7");
  static final String TYPE_LATE_RETURN = "LATE_RETURN";

  LoanRepository loanRepository;
  ClassPeriodRepository classPeriodRepository;
  NotificationRepository notificationRepository;
  UserRepository userRepository;
  DeviceRepository deviceRepository;

  /**
   * Chạy mỗi 10 phút.
   */
  @Scheduled(cron = "0 */10 * * * *")
  @Transactional
  public void scanLateReturns() {
    log.info("Bắt đầu quét trả chậm...");
    LocalDate today = LocalDate.now(ZONE);
    Date start = Date.from(today.atStartOfDay(ZONE).toInstant());
    Date end = Date.from(today.atTime(LocalTime.MAX).atZone(ZONE).toInstant());

    List<LoanEntity> loans = loanRepository.findActiveLoansForLateCheck(start, end);
    if (loans.isEmpty()) {
      return;
    }

    Map<Integer, ClassPeriodEntity> periodMap = classPeriodRepository.findAll().stream()
      .filter(p -> p.getPeriodNumber() != null)
      .collect(Collectors.toMap(ClassPeriodEntity::getPeriodNumber, Function.identity(), (a, b) -> a));

    LocalDateTime now = LocalDateTime.now(ZONE);
    int lateCount = 0;

    for (LoanEntity loan : loans) {
      if (loan.getReturnPeriod() == null || loan.getBorrowDate() == null) {
        continue;
      }
      ClassPeriodEntity period = periodMap.get(loan.getReturnPeriod());
      if (period == null || period.getEndTime() == null) {
        continue;
      }

      // Hạn trả = ngày mượn + giờ kết thúc tiết trả.
      LocalDateTime deadline = loan.getBorrowDate().toInstant().atZone(ZONE).toLocalDate()
        .atTime(period.getEndTime());

      if (!now.isAfter(deadline)) {
        continue; // chưa quá hạn
      }

      long lateMinutes = Math.max(0, Duration.between(deadline, now).toMinutes());

      // Cập nhật phiếu mượn: trả chậm.
      loan.setStatus(3);
      loan.setLateMinutes(lateMinutes);
      loan.setModifiedDate(new Date());
      loanRepository.save(loan);
      lateCount++;

      // Tránh insert trùng: chỉ tạo thông báo nếu chưa có theo ref_id + user_id.
      String borrowerId = loan.getBorrowerId();
      if (notificationRepository.checkDuplicate(loan.getId(), borrowerId)) {
        continue;
      }

      String fullName = userRepository.findById(borrowerId)
        .map(UserEntity::getFullName)
        .orElse("Người mượn");
      String deviceName = loan.getDeviceId() == null ? "thiết bị"
        : deviceRepository.findById(loan.getDeviceId())
        .map(DeviceEntity::getName)
        .orElse("thiết bị");

      NotificationEntity notification = new NotificationEntity();
      notification.setTitle("Trả chậm thiết bị");
      notification.setMessage(fullName + " chưa trả " + deviceName + " - " + lateMinutes + " phút");
      notification.setType(TYPE_LATE_RETURN);
      notification.setRefId(loan.getId());
      notification.setUserId(borrowerId);
      notification.setIsRead(false);
      notificationRepository.save(notification);
    }

    if (lateCount > 0) {
      log.info("Quét trả chậm: cập nhật {} phiếu mượn quá hạn.", lateCount);
    }
  }
}

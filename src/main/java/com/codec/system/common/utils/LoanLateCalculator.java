package com.codec.system.common.utils;

import com.codec.system.domain.entity.ClassPeriodEntity;
import com.codec.system.domain.entity.LoanEntity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * Tính trạng thái "chậm trả" của phiếu mượn một cách động:
 * - Hạn trả = ngày của borrowDate + giờ kết thúc (end_time) của tiết trả (returnPeriod) + ngưỡng phút cấu hình.
 * - Mốc so sánh = thời gian trả thực tế nếu đã trả, ngược lại là thời điểm hiện tại.
 */
public final class LoanLateCalculator {

  private static final ZoneId ZONE = ZoneId.of("GMT+7");

  private LoanLateCalculator() {
  }

  public static class LateResult {
    public final boolean isLate;
    public final long lateMinutes;

    public LateResult(boolean isLate, long lateMinutes) {
      this.isLate = isLate;
      this.lateMinutes = lateMinutes;
    }
  }

  public static LateResult compute(LoanEntity loan,
                                   Map<Integer, ClassPeriodEntity> periodMap,
                                   int thresholdMinutes) {
    if (loan == null || loan.getBorrowDate() == null || loan.getReturnPeriod() == null) {
      return new LateResult(false, 0);
    }
    ClassPeriodEntity period = periodMap.get(loan.getReturnPeriod());
    if (period == null || period.getEndTime() == null) {
      return new LateResult(false, 0);
    }

    LocalTime endTime = period.getEndTime();
    LocalDateTime deadline = toLocalDateTime(loan.getBorrowDate())
      .toLocalDate()
      .atTime(endTime)
      .plusMinutes(thresholdMinutes);

    LocalDateTime reference = loan.getActualReturnDate() != null
      ? toLocalDateTime(loan.getActualReturnDate())
      : LocalDateTime.now(ZONE);

    if (reference.isAfter(deadline)) {
      long minutes = Duration.between(deadline, reference).toMinutes();
      return new LateResult(true, Math.max(0, minutes));
    }
    return new LateResult(false, 0);
  }

  private static LocalDateTime toLocalDateTime(Date date) {
    return date.toInstant().atZone(ZONE).toLocalDateTime();
  }
}

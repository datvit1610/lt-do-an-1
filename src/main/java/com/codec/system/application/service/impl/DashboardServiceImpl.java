package com.codec.system.application.service.impl;

import com.codec.system.application.command.response.dashboard.*;
import com.codec.system.application.service.DashboardService;
import com.codec.system.common.utils.TrendMode;
import com.codec.system.domain.repository.DeviceRepository;
import com.codec.system.domain.repository.LoanRepository;
import com.codec.system.domain.repository.UserRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

  private static final String ROLE_TEACHER = "Giảng viên";
  private static final String ROLE_STUDENT  = "Sinh viên";

  private final DeviceRepository deviceRepository;
  private final LoanRepository loanRepository;
  private final UserRepository userRepository;

  /**
   * Lấy dữ liệu tổng quan hệ thống cho dashboard.
   *
   * @param fromDate ngày bắt đầu filter (nullable → mặc định 30 ngày trước)
   * @param toDate   ngày kết thúc filter  (nullable → mặc định hôm nay cuối ngày)
   */
  @Override
  public DashboardOverviewResponse getOverview(Date fromDate, Date toDate) {

    // ── Xử lý default date range nếu FE không truyền ──
    if (toDate == null) {
      toDate = endOfDay(new Date());
    }
    if (fromDate == null) {
      fromDate = startOfDay(daysAgo(toDate, 30));
    }

    // Đảm bảo toDate luôn là cuối ngày (23:59:59.999)
    toDate   = endOfDay(toDate);
    fromDate = startOfDay(fromDate);

    log.info("[Dashboard] Overview query: fromDate={}, toDate={}", fromDate, toDate);

    // ── Thiết bị ──
    Long totalDeviceTypes    = deviceRepository.countActiveDevices();
    Long totalDeviceQuantity = deviceRepository.sumActiveDeviceQuantity();

    // ── Phiếu mượn ──
    Long totalLoans = loanRepository.countLoansInRange(fromDate, toDate);
    Long totalLost  = loanRepository.countLostInRange(fromDate, toDate);

    // ── Người dùng ──
    Long totalTeachers = userRepository.countByRoleName(ROLE_TEACHER);
    Long totalStudents = userRepository.countByRoleName(ROLE_STUDENT);

    return DashboardOverviewResponse.builder()
      .totalDeviceTypes(totalDeviceTypes)
      .totalDeviceQuantity(totalDeviceQuantity)
      .totalLoans(totalLoans)
      .totalLost(totalLost)
      .totalTeachers(totalTeachers)
      .totalStudents(totalStudents)
      .fromDate(fromDate)
      .toDate(toDate)
      .build();
  }


  /** Top 5 thiết bị được mượn nhiều nhất */
  @Override
  public List<Top5DeviceResponse> getTop5BorrowedDevices(Date fromDate, Date toDate) {
    toDate   = toDate   == null ? endOfDay(new Date())            : endOfDay(toDate);
    fromDate = fromDate == null ? startOfDay(daysAgo(toDate, 30)) : startOfDay(fromDate);

    log.info("[Dashboard] Top5 fromDate={}, toDate={}", fromDate, toDate);

    return loanRepository.findTop5BorrowedDevices(fromDate, toDate).stream().map(Top5DeviceResponse::new).toList();
  }

  /**
   * Thống kê phiếu mượn theo trạng thái — dùng cho biểu đồ tròn.
   * Native query trả về Object[], parse thủ công để tương thích
   * với cả MySQL (BigInteger/BigDecimal) và H2 test (Long).
   */
  @Override
  public LoanStatusResponse getLoanStatusStats(Date fromDate, Date toDate) {
    toDate   = toDate   == null ? endOfDay(new Date())            : endOfDay(toDate);
    fromDate = fromDate == null ? startOfDay(daysAgo(toDate, 30)) : startOfDay(fromDate);
    log.info("[Dashboard] LoanStatus fromDate={}, toDate={}", fromDate, toDate);

    Tuple tuple = loanRepository.countLoansByStatus(fromDate, toDate);
    return new LoanStatusResponse(tuple);
  }

  /**
   * Xu hướng mượn theo thời gian — biểu đồ line.
   *
   * @param mode "day" | "week" | "month"
   */
  @Override
  public LoanTrendResponse getLoanTrend(Date fromDate, Date toDate, TrendMode mode) {
    toDate   = toDate   == null ? endOfDay(new Date())            : endOfDay(toDate);
    fromDate = fromDate == null ? startOfDay(daysAgo(toDate, 30)) : startOfDay(fromDate);
    log.info("[Dashboard] LoanTrend mode={}, fromDate={}, toDate={}", mode, fromDate, toDate);

    List<Object[]> rows = switch (mode) {
      case DAY   -> loanRepository.findTrendByDay(fromDate, toDate);
      case WEEK  -> loanRepository.findTrendByWeek(fromDate, toDate);
      case MONTH -> loanRepository.findTrendByMonth(fromDate, toDate);
    };

    List<LoanTrendResponse.TrendPoint> points = rows.stream()
      .map(row -> new LoanTrendResponse.TrendPoint(
        formatLabel(row[0].toString(), mode),
        toLong(row[1])
      ))
      .toList();

    // ── Tính stat cards ──
    long total = points.stream().mapToLong(LoanTrendResponse.TrendPoint::getLoans).sum();
    long peak  = points.stream().mapToLong(LoanTrendResponse.TrendPoint::getLoans).max().orElse(0L);
    String peakLabel = points.stream()
      .filter(p -> p.getLoans() == peak)
      .map(LoanTrendResponse.TrendPoint::getLabel)
      .findFirst().orElse("");
    long avg = points.isEmpty() ? 0L : Math.round((double) total / points.size());

    return new LoanTrendResponse(points, total, peak, peakLabel, avg);
  }


  /** Thống kê lượt mượn theo loại thiết bị — biểu đồ donut */
  @Override
  public DeviceTypeLoanResponse getLoansByDeviceType(Date fromDate, Date toDate) {
    toDate   = toDate   == null ? endOfDay(new Date())            : endOfDay(toDate);
    fromDate = fromDate == null ? startOfDay(daysAgo(toDate, 30)) : startOfDay(fromDate);
    log.info("[Dashboard] DeviceType fromDate={}, toDate={}", fromDate, toDate);

    List<Object[]> rows = loanRepository.findLoansByDeviceType(fromDate, toDate);

    long total = rows.stream().mapToLong(r -> toLong(r[1])).sum();

    List<DeviceTypeLoanResponse.DeviceTypeItem> items = rows.stream()
      .map(row -> {
        String type     = row[0] != null ? row[0].toString() : "Khác";
        long   loans    = toLong(row[1]);
        double pct = total == 0 ? 0.0
          : Math.round(loans * 1000.0 / total) / 10.0;
        return new DeviceTypeLoanResponse.DeviceTypeItem(type, loans, pct);
      })
      .toList();

    String mostPopular = items.isEmpty() ? "" : items.get(0).getDeviceType();

    return new DeviceTypeLoanResponse(items, total, mostPopular);
  }

  /**
   * @param roleName null = tất cả | "Sinh viên" | "Giảng viên"
   * @param topN     số lượng kết quả, mặc định 10
   */
  @Override
  public List<TopBorrowerResponse> getTopBorrowers(Date fromDate, Date toDate,
                                                   String roleName, int topN) {
    toDate   = toDate   == null ? endOfDay(new Date())            : endOfDay(toDate);
    fromDate = fromDate == null ? startOfDay(daysAgo(toDate, 30)) : startOfDay(fromDate);
    if (topN <= 0) topN = 10;

    // Chuẩn hóa roleName rỗng về null để query không filter
    if (roleName != null && roleName.isBlank()) roleName = null;

    log.info("[TopBorrower] fromDate={}, toDate={}, roleName={}, topN={}",
      fromDate, toDate, roleName, topN);

    return loanRepository.findTopBorrowers(fromDate, toDate, roleName, topN).stream().map(TopBorrowerResponse::new).toList();
  }

  // ── Helpers ──────────────────────────────────────────

  /**
   * Format label hiển thị trên trục X tuỳ theo mode.
   * day:   "2026-06-01" → "01/06"
   * week:  "202623"     → "T2/06"   (tuần 23 của 2026, lấy ngày đầu tuần)
   * month: "2026-06"    → "Th6/26"
   */
  private String formatLabel(String rawStr, TrendMode mode) {
    try {
      return switch (mode) {
        case DAY -> {
          // raw = "2026-06-01"
          String[] p = rawStr.split("-");
          yield p[2] + "/" + p[1];
        }
        case WEEK -> {
          // PostgreSQL EXTRACT trả Double, VD: 202623.0
          long raw  = Double.valueOf(rawStr).longValue();
          int year  = (int) (raw / 100);
          int week  = (int) (raw % 100);
          Calendar cal = Calendar.getInstance();
          cal.setMinimalDaysInFirstWeek(4);
          cal.setFirstDayOfWeek(Calendar.MONDAY);
          cal.set(Calendar.YEAR, year);
          cal.set(Calendar.WEEK_OF_YEAR, week);
          cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
          yield String.format("T%d/%02d",
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1);
        }
        case MONTH -> {
          // raw = "2026-06"
          String[] p = rawStr.split("-");
          yield "Th" + Integer.parseInt(p[1]) + "/" + p[0].substring(2);
        }
      };
    } catch (Exception e) {
      return rawStr;
    }
  }

  /** MySQL native query trả BigInteger — chuẩn hóa về Long */
  private Long toLong(Object val) {
    if (val == null)           return 0L;
    if (val instanceof Long)   return (Long) val;
    if (val instanceof BigInteger) return ((BigInteger) val).longValue();
    return ((Number) val).longValue();
  }

  /** Đầu ngày: 00:00:00.000 */
  private Date startOfDay(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime();
  }

  /** Cuối ngày: 23:59:59.999 */
  private Date endOfDay(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 999);
    return cal.getTime();
  }

  /** N ngày trước tính từ date */
  private Date daysAgo(Date date, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DAY_OF_MONTH, -days);
    return cal.getTime();
  }
}


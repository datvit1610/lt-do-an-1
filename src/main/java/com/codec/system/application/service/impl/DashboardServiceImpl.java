package com.codec.system.application.service.impl;

import com.codec.system.application.command.response.dashboard.DashboardOverviewResponse;
import com.codec.system.application.command.response.dashboard.LoanStatusResponse;
import com.codec.system.application.command.response.dashboard.Top5DeviceResponse;
import com.codec.system.application.service.DashboardService;
import com.codec.system.domain.repository.DeviceRepository;
import com.codec.system.domain.repository.LoanRepository;
import com.codec.system.domain.repository.UserRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

  // ── Helpers ──────────────────────────────────────────

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


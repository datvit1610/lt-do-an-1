package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.application.command.request.loan.CreateLoanRequest;
import com.codec.system.application.command.request.loan.ReturnLoanRequest;
import com.codec.system.application.command.request.loan.UpdateLoanRequest;
import com.codec.system.application.command.response.loan.LoanResponse;
import com.codec.system.application.service.LoanConfigService;
import com.codec.system.application.service.LoanService;
import com.codec.system.common.utils.LoanLateCalculator;
import com.codec.system.domain.entity.ClassPeriodEntity;
import com.codec.system.domain.entity.LoanEntity;
import com.codec.system.domain.repository.ClassPeriodRepository;
import com.codec.system.domain.repository.DeviceRepository;
import com.codec.system.domain.repository.LoanRepository;
import jakarta.persistence.Tuple;
import com.codec.system.pagination.domain.CodecSystemApplicationPage;
import com.codec.system.pagination.domain.CodecSystemApplicationPageable;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {
  LoanRepository loanRepository;
  ClassPeriodRepository classPeriodRepository;
  LoanConfigService loanConfigService;
  DeviceRepository deviceRepository;

  private static final ZoneId ZONE = ZoneId.of("GMT+7");
  private static final DateTimeFormatter LOAN_CODE_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss");

  private Map<Integer, ClassPeriodEntity> loadPeriodMap() {
    return classPeriodRepository.findAll().stream()
      .filter(p -> p.getPeriodNumber() != null)
      .collect(Collectors.toMap(ClassPeriodEntity::getPeriodNumber, Function.identity(), (a, b) -> a));
  }

  /**
   * Sinh mã phiếu mượn duy nhất, dạng PM + yyMMddHHmmss (vd: PM260608153045).
   * Nếu trùng (cùng giây) thì thêm hậu tố tăng dần để đảm bảo duy nhất.
   */
  private String generateLoanCode() {
    String base = "PM" + LocalDateTime.now(ZONE).format(LOAN_CODE_FORMATTER);
    String code = base;
    int suffix = 1;
    while (loanRepository.existsByLoanCode(code)) {
      code = base + "-" + suffix++;
    }
    return code;
  }

  private static Integer asInt(Object v) {
    return v == null ? null : ((Number) v).intValue();
  }

  private static Date asDate(Object v) {
    return v == null ? null : (Date) v;
  }

  private static String asStr(Object v) {
    return v == null ? null : v.toString();
  }

//  private LoanResponse toResponse(Tuple t,
//                                  Map<Integer, ClassPeriodEntity> periodMap,
//                                  int thresholdMinutes) {
//    LoanResponse r = new LoanResponse();
//    r.setId(asStr(t.get("id")));
//    r.setLoanCode(asStr(t.get("loanCode")));
//    r.setBorrowerId(asStr(t.get("borrowerId")));
//    r.setBorrowerName(asStr(t.get("borrowerName")));
//    r.setDeviceId(asStr(t.get("deviceId")));
//    r.setItemCode(asStr(t.get("itemCode")));
//    r.setItemName(asStr(t.get("itemName")));
//    r.setQuantity(asInt(t.get("quantity")));
//    Date borrowDate = asDate(t.get("borrowDate"));
//    r.setBorrowDate(borrowDate);
//    r.setBorrowPeriod(asInt(t.get("borrowPeriod")));
//    Integer returnPeriod = asInt(t.get("returnPeriod"));
//    r.setReturnPeriod(returnPeriod);
//    Date actualReturnDate = asDate(t.get("actualReturnDate"));
//    r.setActualReturnDate(actualReturnDate);
//    Integer status = asInt(t.get("status"));
//    r.setNote(asStr(t.get("note")));
//    r.setCreatedDate(asDate(t.get("createdDate")));
//    r.setModifiedDate(asDate(t.get("modifiedDate")));
//
//    // Tính chậm trả động dựa trên borrowDate + tiết trả + ngưỡng cấu hình.
//    LoanEntity tmp = new LoanEntity();
//    tmp.setBorrowDate(borrowDate);
//    tmp.setReturnPeriod(returnPeriod);
//    tmp.setActualReturnDate(actualReturnDate);
//    LoanLateCalculator.LateResult late = LoanLateCalculator.compute(tmp, periodMap, thresholdMinutes);
//    r.setLateMinutes(late.lateMinutes);
//
//    // Phản ánh trạng thái chậm trả trực tiếp vào status (3 - Trả chậm),
//    // trừ khi thiết bị đã được đánh dấu mất (4 - Mất thiết bị).
//    if (!Integer.valueOf(4).equals(status) && late.isLate) {
//      status = 3;
//    }
//    r.setStatus(status);
//    return r;
//  }

  @Override
  public Response<RestCodecSystemApplicationPage<LoanResponse>> getAllLoan(String loanCode, String borrowerName,
                                                                           Integer status, Integer approveStatus,
                                                                           LocalDate fromDate,
                                                                           LocalDate toDate, Pageable pageable) {
    // Lọc theo khoảng ngày mượn: from = đầu ngày, to = cuối ngày (bao trọn cả ngày).
    Date from = fromDate != null ? Date.from(fromDate.atStartOfDay(ZONE).toInstant()) : null;
    Date to = toDate != null ? Date.from(toDate.atTime(LocalTime.MAX).atZone(ZONE).toInstant()) : null;

    Page<Tuple> page = loanRepository.getAllLoan(loanCode, borrowerName, status, approveStatus, from, to, pageable);
    Map<Integer, ClassPeriodEntity> periodMap = loadPeriodMap();
    int threshold = loanConfigService.getLateThresholdMinutes();

    List<LoanResponse> responses = page.stream()
      .map(LoanResponse::new)
      .toList();

    responses.forEach(r -> {
      // Tính chậm trả động dựa trên borrowDate + tiết trả + ngưỡng cấu hình.
      LoanEntity tmp = new LoanEntity();
      tmp.setBorrowDate(r.getBorrowDate());
      tmp.setReturnPeriod(r.getReturnPeriod());
      tmp.setActualReturnDate(r.getActualReturnDate());
      LoanLateCalculator.LateResult late = LoanLateCalculator.compute(tmp, periodMap, threshold);
      r.setLateMinutes(late.lateMinutes);

      // Phản ánh trạng thái chậm trả trực tiếp vào status (3 - Trả chậm),
      // trừ khi thiết bị đã được đánh dấu mất (4 - Mất thiết bị).
      if (!Integer.valueOf(4).equals(r.getStatus()) && late.isLate) {
        r.setStatus(3);
      }
    });

    long currentCount = page.getTotalElements();
    CodecSystemApplicationPageable codecPageable = new CodecSystemApplicationPageable(pageable.getPageNumber(), pageable.getPageSize());

    RestCodecSystemApplicationPage<LoanResponse> rest = RestCodecSystemApplicationPage.from(
      CodecSystemApplicationPage.of(responses, codecPageable, currentCount), d -> d);

    return Response.of(rest).success("Thành công", 200);
  }

  @Override
  public Response<RestCodecSystemApplicationPage<LoanResponse>> getAllLoanForUser(String loanCode,
                                                                           Integer status, LocalDate fromDate,
                                                                           LocalDate toDate, Pageable pageable, String userId) {
    // Lọc theo khoảng ngày mượn: from = đầu ngày, to = cuối ngày (bao trọn cả ngày).
    Date from = fromDate != null ? Date.from(fromDate.atStartOfDay(ZONE).toInstant()) : null;
    Date to = toDate != null ? Date.from(toDate.atTime(LocalTime.MAX).atZone(ZONE).toInstant()) : null;

    Page<Tuple> page = loanRepository.getAllLoanForUser(loanCode, status, from, to, pageable, userId);
    Map<Integer, ClassPeriodEntity> periodMap = loadPeriodMap();
    int threshold = loanConfigService.getLateThresholdMinutes();

    List<LoanResponse> responses = page.stream()
      .map(LoanResponse::new)
      .toList();

    responses.forEach(r -> {
      // Tính chậm trả động dựa trên borrowDate + tiết trả + ngưỡng cấu hình.
      LoanEntity tmp = new LoanEntity();
      tmp.setBorrowDate(r.getBorrowDate());
      tmp.setReturnPeriod(r.getReturnPeriod());
      tmp.setActualReturnDate(r.getActualReturnDate());
      LoanLateCalculator.LateResult late = LoanLateCalculator.compute(tmp, periodMap, threshold);
      r.setLateMinutes(late.lateMinutes);

      // Phản ánh trạng thái chậm trả trực tiếp vào status (3 - Trả chậm),
      // trừ khi thiết bị đã được đánh dấu mất (4 - Mất thiết bị).
      if (!Integer.valueOf(4).equals(r.getStatus()) && late.isLate) {
        r.setStatus(3);
      }
    });

    long currentCount = page.getTotalElements();
    CodecSystemApplicationPageable codecPageable = new CodecSystemApplicationPageable(pageable.getPageNumber(), pageable.getPageSize());

    RestCodecSystemApplicationPage<LoanResponse> rest = RestCodecSystemApplicationPage.from(
      CodecSystemApplicationPage.of(responses, codecPageable, currentCount), d -> d);

    return Response.of(rest).success("Thành công", 200);
  }
  @Override
  @Transactional
  public void createLoan(CreateLoanRequest request, String userId) {
    try {
      Integer borrowPeriod = request.getBorrowPeriod();
      Integer returnPeriod = request.getReturnPeriod();
      if (borrowPeriod == null || returnPeriod == null) {
        throw new RuntimeException("Cần nhập tiết mượn và tiết trả");
      }
      if (borrowPeriod < 1 || borrowPeriod > 14 || returnPeriod < 1 || returnPeriod > 14) {
        throw new RuntimeException("Tiết mượn/tiết trả phải nằm trong khoảng 1-14");
      }
      if (returnPeriod < borrowPeriod) {
        throw new RuntimeException("Tiết trả phải lớn hơn hoặc bằng tiết mượn");
      }
      if (request.getDeviceId() == null || request.getDeviceId().isBlank()) {
        throw new RuntimeException("Cần chọn thiết bị mượn");
      }
      if (!deviceRepository.existsById(request.getDeviceId())) {
        throw new RuntimeException("Thiết bị không tồn tại");
      }

      LoanEntity loan = new LoanEntity();
      loan.setLoanCode(generateLoanCode());
      loan.setBorrowerId(request.getBorrowerId() != null ? request.getBorrowerId() : userId);
      loan.setDeviceId(request.getDeviceId());
      loan.setQuantity(request.getQuantity());
      loan.setBorrowDate(new Date());
      loan.setBorrowPeriod(borrowPeriod);
      loan.setReturnPeriod(returnPeriod);
      loan.setStatus(1);
      loan.setNote(request.getNote());
      loan.setCreatedBy(userId);
      loanRepository.save(loan);
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }

  @Override
  @Transactional
  public void updateLoan(String id, UpdateLoanRequest request, String userId) {
    try {
      LoanEntity l = loanRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Phiếu mượn không tồn tại"));

      // Chỉ cho phép cập nhật khi phiếu chưa trả (status != 2) và chưa mất (status != 4)
      if (Integer.valueOf(2).equals(l.getStatus()) || Integer.valueOf(4).equals(l.getStatus())) {
        throw new RuntimeException("Phiếu mượn đã kết thúc, không thể cập nhật");
      }

      l.setActualReturnDate(new Date());
      l.setNote(request.getNote());
      l.setModifiedDate(new Date());
      l.setModifiedBy(userId);

      // Tính late dựa trên borrowDate + tiết trả + ngưỡng cấu hình
      Map<Integer, ClassPeriodEntity> periodMap = loadPeriodMap();
      int threshold = loanConfigService.getLateThresholdMinutes();
      LoanLateCalculator.LateResult late = LoanLateCalculator.compute(l, periodMap, threshold);
      // Lưu số phút trả chậm vào entity
      l.setLateMinutes(late.lateMinutes);
      // Ưu tiên status từ request nếu là "Mất thiết bị" (4),
      // còn lại tự động tính: chậm trả (3) hoặc trả đúng hạn (2)
      if (Integer.valueOf(2).equals(request.getStatus())) {
        l.setStatus(4); // Mất thiết bị — giữ nguyên theo request
      } else if (late.isLate) {
        l.setStatus(3); // Trả chậm
      } else {
        l.setStatus(2); // Trả đúng hạn
      }

      loanRepository.save(l);
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }

  @Override
  @Transactional
  public void returnLoan(String id, ReturnLoanRequest request, String userId) {
    try {
      Optional<LoanEntity> entity = loanRepository.findById(id);
      if (entity.isEmpty() || Boolean.TRUE.equals(entity.get().getDeleted())) {
        throw new RuntimeException("Phiếu mượn không tồn tại");
      }
      LoanEntity l = entity.get();
      l.setActualReturnDate(request.getActualReturnDate() != null ? request.getActualReturnDate() : new Date());

      // Ghi trạng thái trả trực tiếp vào status: 3 - Trả chậm nếu quá hạn, ngược lại 2 - đã trả.
      Map<Integer, ClassPeriodEntity> periodMap = loadPeriodMap();
      int threshold = loanConfigService.getLateThresholdMinutes();
      LoanLateCalculator.LateResult late = LoanLateCalculator.compute(l, periodMap, threshold);
      l.setStatus(late.isLate ? 3 : 2);

      l.setModifiedDate(new Date());
      l.setModifiedBy(userId);
      loanRepository.save(l);
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }

  @Override
  @Transactional
  public void deleteLoan(String id, String userId) {
    try {
      Optional<LoanEntity> entity = loanRepository.findById(id);
      if (entity.isEmpty()) {
        throw new RuntimeException("Phiếu mượn không tồn tại");
      }
      LoanEntity l = entity.get();
      l.setDeleted(true);
      l.setModifiedDate(new Date());
      l.setModifiedBy(userId);
      loanRepository.save(l);
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }

  @Override
  @Transactional
  public void approveLoan(String id, Integer approveStatus, String userId) {
    try {
      // Chỉ chấp nhận duyệt (1) hoặc hủy (2); không cho set về 0 (chưa duyệt).
      if (approveStatus == null || (approveStatus != 1 && approveStatus != 2)) {
        throw new RuntimeException("Trạng thái duyệt không hợp lệ (chỉ nhận 1 - duyệt hoặc 2 - hủy)");
      }
      LoanEntity l = loanRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Phiếu mượn không tồn tại"));
      if (Boolean.TRUE.equals(l.getDeleted())) {
        throw new RuntimeException("Phiếu mượn không tồn tại");
      }

      l.setApproveStatus(approveStatus);
      l.setApprovedBy(userId);
      l.setApprovedDate(new Date());
      l.setModifiedDate(new Date());
      l.setModifiedBy(userId);
      loanRepository.save(l);
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }
}

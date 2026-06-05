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
import com.codec.system.domain.repository.LoanRepository;
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

  private Map<Integer, ClassPeriodEntity> loadPeriodMap() {
    return classPeriodRepository.findAll().stream()
      .filter(p -> p.getPeriodNumber() != null)
      .collect(Collectors.toMap(ClassPeriodEntity::getPeriodNumber, Function.identity(), (a, b) -> a));
  }

  private LoanResponse toResponse(LoanEntity entity,
                                  Map<Integer, ClassPeriodEntity> periodMap,
                                  int thresholdMinutes) {
    LoanResponse r = new LoanResponse();
    r.setId(entity.getId());
    r.setBorrowerId(entity.getBorrowerId());
    r.setDeviceId(entity.getDeviceId());
    r.setItemName(entity.getItemName());
    r.setQuantity(entity.getQuantity());
    r.setBorrowDate(entity.getBorrowDate());
    r.setBorrowPeriod(entity.getBorrowPeriod());
    r.setReturnPeriod(entity.getReturnPeriod());
    r.setActualReturnDate(entity.getActualReturnDate());
    r.setStatus(entity.getStatus());
    r.setNote(entity.getNote());
    r.setCreatedDate(entity.getCreatedDate());
    r.setModifiedDate(entity.getModifiedDate());

    LoanLateCalculator.LateResult late = LoanLateCalculator.compute(entity, periodMap, thresholdMinutes);
    r.setIsLate(late.isLate);
    r.setLateMinutes(late.lateMinutes);
    return r;
  }

  @Override
  public Response<RestCodecSystemApplicationPage<LoanResponse>> getAllLoan(Pageable pageable) {
    Page<LoanEntity> page = loanRepository.findAll(pageable);
    Map<Integer, ClassPeriodEntity> periodMap = loadPeriodMap();
    int threshold = loanConfigService.getLateThresholdMinutes();

    List<LoanResponse> responses = page.stream()
      .map(entity -> toResponse(entity, periodMap, threshold))
      .toList();

    long currentCount = page.getTotalElements();
    CodecSystemApplicationPageable codecPageable = new CodecSystemApplicationPageable(pageable.getPageNumber(), pageable.getPageSize());

    RestCodecSystemApplicationPage<LoanResponse> rest = RestCodecSystemApplicationPage.from(
      CodecSystemApplicationPage.of(responses, codecPageable, currentCount), d -> d);

    return Response.of(rest).success("Thành công", 200);
  }

  @Override
  public Response<LoanResponse> getLoanById(String id) {
    Optional<LoanEntity> entity = loanRepository.findById(id);
    if (entity.isEmpty() || Boolean.TRUE.equals(entity.get().getDeleted())) {
      throw new RuntimeException("Phiếu mượn không tồn tại");
    }
    Map<Integer, ClassPeriodEntity> periodMap = loadPeriodMap();
    int threshold = loanConfigService.getLateThresholdMinutes();
    return Response.of(toResponse(entity.get(), periodMap, threshold)).success("Thành công", 200);
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

      LoanEntity loan = new LoanEntity();
      loan.setBorrowerId(request.getBorrowerId() != null ? request.getBorrowerId() : userId);
      loan.setDeviceId(request.getDeviceId());
      loan.setItemName(request.getItemName());
      loan.setQuantity(request.getQuantity());
      loan.setBorrowDate(request.getBorrowDate());
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
      Optional<LoanEntity> entity = loanRepository.findById(id);
      if (entity.isEmpty()) {
        throw new RuntimeException("Phiếu mượn không tồn tại");
      }
      LoanEntity l = entity.get();
      l.setItemName(request.getItemName());
      l.setQuantity(request.getQuantity());
      l.setBorrowDate(request.getBorrowDate());
      l.setBorrowPeriod(request.getBorrowPeriod());
      l.setReturnPeriod(request.getReturnPeriod());
      l.setActualReturnDate(request.getActualReturnDate());
      l.setStatus(request.getStatus());
      l.setNote(request.getNote());
      l.setModifiedDate(new Date());
      l.setModifiedBy(userId);
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
      l.setStatus(2);
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
}

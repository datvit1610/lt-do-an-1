package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.application.command.request.loan.CreateLoanRequest;
import com.codec.system.application.command.request.loan.UpdateLoanRequest;
import com.codec.system.application.command.response.loan.LoanResponse;
import com.codec.system.application.service.LoanService;
import com.codec.system.domain.entity.LoanEntity;
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
import java.util.Optional;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {
  LoanRepository loanRepository;

  @Override
  public Response<RestCodecSystemApplicationPage<LoanResponse>> getAllLoan(Pageable pageable) {
    Page<LoanEntity> page = loanRepository.findAll(pageable);
    List<LoanResponse> responses = page.stream().map(entity -> {
      LoanResponse r = new LoanResponse();
      r.setId(entity.getId());
      r.setBorrowerId(entity.getBorrowerId());
      r.setDeviceId(entity.getDeviceId());
      r.setItemName(entity.getItemName());
      r.setQuantity(entity.getQuantity());
      r.setBorrowDate(entity.getBorrowDate());
      r.setExpectedReturnDate(entity.getExpectedReturnDate());
      r.setActualReturnDate(entity.getActualReturnDate());
      r.setStatus(entity.getStatus());
      r.setNote(entity.getNote());
      r.setCreatedDate(entity.getCreatedDate());
      r.setModifiedDate(entity.getModifiedDate());
      return r;
    }).toList();

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
    LoanEntity e = entity.get();
    LoanResponse r = new LoanResponse();
    r.setId(e.getId());
    r.setBorrowerId(e.getBorrowerId());
    r.setDeviceId(e.getDeviceId());
    r.setItemName(e.getItemName());
    r.setQuantity(e.getQuantity());
    r.setBorrowDate(e.getBorrowDate());
    r.setExpectedReturnDate(e.getExpectedReturnDate());
    r.setActualReturnDate(e.getActualReturnDate());
    r.setStatus(e.getStatus());
    r.setNote(e.getNote());
    r.setCreatedDate(e.getCreatedDate());
    r.setModifiedDate(e.getModifiedDate());
    return Response.of(r).success("Thành công", 200);
  }

  @Override
  @Transactional
  public void createLoan(CreateLoanRequest request, String userId) {
    try {
      LoanEntity loan = new LoanEntity();
      loan.setBorrowerId(request.getBorrowerId() != null ? request.getBorrowerId() : userId);
      loan.setDeviceId(request.getDeviceId());
      loan.setItemName(request.getItemName());
      loan.setQuantity(request.getQuantity());
      loan.setBorrowDate(request.getBorrowDate());
      loan.setExpectedReturnDate(request.getExpectedReturnDate());
      loan.setStatus(1);
      loan.setNote(request.getNote());
      loan.setCreatedBy(userId);
      loanRepository.save(loan);
      Response.ok();
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
      l.setExpectedReturnDate(request.getExpectedReturnDate());
      l.setActualReturnDate(request.getActualReturnDate());
      l.setStatus(request.getStatus());
      l.setNote(request.getNote());
      l.setModifiedDate(new Date());
      l.setModifiedBy(userId);
      loanRepository.save(l);
      Response.ok();
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
      Response.ok();
    } catch (Exception e) {
      throw new RuntimeException("Gặp lỗi: " + e.getMessage());
    }
  }
}

package com.codec.system.application.service;

import codec.common.Response;
import com.codec.system.application.command.request.loan.CreateLoanRequest;
import com.codec.system.application.command.request.loan.UpdateLoanRequest;
import com.codec.system.application.command.response.loan.LoanResponse;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import org.springframework.data.domain.Pageable;

public interface LoanService {
  Response<RestCodecSystemApplicationPage<LoanResponse>> getAllLoan(Pageable pageable);
  Response<LoanResponse> getLoanById(String id);
  void createLoan(CreateLoanRequest request, String userId);
  void updateLoan(String id, UpdateLoanRequest request, String userId);
  void deleteLoan(String id, String userId);
}

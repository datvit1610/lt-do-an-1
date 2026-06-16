package com.codec.system.application.service;

import codec.common.Response;
import com.codec.system.application.command.request.loan.CreateLoanRequest;
import com.codec.system.application.command.request.loan.ReturnLoanRequest;
import com.codec.system.application.command.request.loan.UpdateLoanRequest;
import com.codec.system.application.command.response.loan.LoanResponse;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface LoanService {
  Response<RestCodecSystemApplicationPage<LoanResponse>> getAllLoan(String loanCode, String borrowerName,
                                                                    Integer status, Integer approveStatus,
                                                                    LocalDate fromDate,
                                                                    LocalDate toDate, Pageable pageable);
  Response<RestCodecSystemApplicationPage<LoanResponse>> getAllLoanForUser(String loanCode,
                                                                    Integer status, LocalDate fromDate,
                                                                    LocalDate toDate, Pageable pageable, String userId);
  void createLoan(CreateLoanRequest request, String userId);
  void updateLoan(String id, UpdateLoanRequest request, String userId);
  void returnLoan(String id, ReturnLoanRequest request, String userId);
  void deleteLoan(String id, String userId);
  void approveLoan(String id, Integer approveStatus, String userId);
}

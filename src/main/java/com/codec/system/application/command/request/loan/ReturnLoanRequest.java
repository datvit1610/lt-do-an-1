package com.codec.system.application.command.request.loan;

import lombok.Data;

import java.util.Date;

@Data
public class ReturnLoanRequest {
  // Thời gian trả thực tế
  private Date actualReturnDate;
}

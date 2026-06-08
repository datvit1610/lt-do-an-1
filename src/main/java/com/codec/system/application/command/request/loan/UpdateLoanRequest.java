package com.codec.system.application.command.request.loan;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateLoanRequest {
  // Trạng thái: 1 - trả đồ, 2 - mất đồ
  private Integer status;

  // Ghi chú
  private String note;
}

package com.codec.system.application.command.request.loan;

import lombok.Data;

@Data
public class ApproveLoanRequest {
  // Trạng thái duyệt: 1 - đã duyệt, 2 - hủy (không nhận 0)
  private Integer approveStatus;
}

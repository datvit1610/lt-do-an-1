package com.codec.system.application.command.request.loan;

import lombok.Data;

import java.util.Date;

@Data
public class CreateLoanRequest {
  // Id người mượn
  private String borrowerId;

  // Id thiết bị (nếu có)
  private String deviceId;

  // Tên món đồ mượn
  private String itemName;

  // Số lượng
  private Integer quantity;

  // Ngày bắt đầu mượn
  private Date borrowDate;

  // Ngày trả dự kiến
  private Date expectedReturnDate;

  // Ghi chú
  private String note;
}

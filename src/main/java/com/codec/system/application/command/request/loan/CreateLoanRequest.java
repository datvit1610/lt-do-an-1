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

  // Thời gian mượn (ngày + giờ)
  private Date borrowDate;

  // Tiết mượn
  private Integer borrowPeriod;

  // Tiết trả (mốc tính hạn trả)
  private Integer returnPeriod;

  // Ghi chú
  private String note;
}

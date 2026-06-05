package com.codec.system.application.command.request.loan;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateLoanRequest {
  // Tên món đồ mượn
  private String itemName;

  // Số lượng
  private Integer quantity;

  // Thời gian mượn
  private Date borrowDate;

  // Tiết mượn
  private Integer borrowPeriod;

  // Tiết trả
  private Integer returnPeriod;

  // Thời gian trả thực tế
  private Date actualReturnDate;

  // Trạng thái: 1 - đang mượn, 2 - đã trả
  private Integer status;

  // Ghi chú
  private String note;
}

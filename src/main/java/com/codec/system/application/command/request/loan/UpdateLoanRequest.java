package com.codec.system.application.command.request.loan;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateLoanRequest {
  // Tên món đồ mượn
  private String itemName;

  // Số lượng
  private Integer quantity;

  // Ngày bắt đầu mượn
  private Date borrowDate;

  // Ngày trả dự kiến
  private Date expectedReturnDate;

  // Ngày trả thực tế
  private Date actualReturnDate;

  // Trạng thái: 1 - đang mượn, 2 - đã trả
  private Integer status;

  // Ghi chú
  private String note;
}

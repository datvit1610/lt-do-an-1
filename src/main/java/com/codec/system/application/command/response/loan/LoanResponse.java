package com.codec.system.application.command.response.loan;

import lombok.Data;

import java.util.Date;

@Data
public class LoanResponse {
  private String id;
  private String borrowerId;
  private String deviceId;
  private String itemName;
  private Integer quantity;
  private Date borrowDate;
  private Integer borrowPeriod;
  private Integer returnPeriod;
  private Date actualReturnDate;
  private Integer status;
  private String note;
  // Trạng thái chậm trả tính động theo masterdata tiết + ngưỡng cấu hình
  private Boolean isLate;
  private Long lateMinutes;
  private Date createdDate;
  private Date modifiedDate;
}

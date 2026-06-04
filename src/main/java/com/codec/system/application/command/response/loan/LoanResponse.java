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
  private Date expectedReturnDate;
  private Date actualReturnDate;
  private Integer status;
  private String note;
  private Date createdDate;
  private Date modifiedDate;
}

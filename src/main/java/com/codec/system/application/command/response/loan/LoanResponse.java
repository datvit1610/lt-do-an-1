package com.codec.system.application.command.response.loan;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Tuple;
import lombok.Data;

import java.util.Date;

@Data
public class LoanResponse {
  private String id;
  private String loanCode; //Mã phiếu mượn
  private String borrowerId;
  private String borrowerName; //Người mượn
  private String deviceId;
  private String itemCode; //Mã thiết bị (join bảng device)
  private String itemName; //Tên thiết bị (join bảng device)
  private Integer quantity;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
  private Date borrowDate; // Thời gian mượn
  private Integer borrowPeriod; // Tiết mượn (số tiết tham chiếu class_periods)
  private Integer returnPeriod; // Tiết trả (mốc tính hạn trả - giờ kết thúc tiết này)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
  private Date actualReturnDate; // Thời gian trả thực tế (điền khi trả)
  // Trạng thái: 1 - đang mượn, 2 - đã trả, 3 - Trả chậm, 4 - Mất thiết bị
  private Integer status;
  private Integer statusSaving; //Trạng thái khi lưu tạm: 1 - đang mượn, 2 - đã trả, 3 - Trả chậm, 4 - Mất thiết bị
  private String note;
  // Số phút trả chậm (tính động theo masterdata tiết + ngưỡng cấu hình)
  private Long lateMinutes; //Số phút trả chậm
  private Date createdDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
  private Date modifiedDate;

  public LoanResponse(Tuple tuple) {
    this.id = tuple.get("id", String.class);
    this.loanCode = tuple.get("loanCode", String.class);
    this.borrowerId = tuple.get("borrowerId", String.class);
    this.borrowerName = tuple.get("borrowerName", String.class);
    this.deviceId = tuple.get("deviceId", String.class);
    this.itemCode = tuple.get("itemCode", String.class);
    this.itemName = tuple.get("itemName", String.class);
    this.quantity = tuple.get("quantity", Integer.class);
    this.borrowDate = tuple.get("borrowDate", Date.class);
    this.borrowPeriod = tuple.get("borrowPeriod", Integer.class);
    this.returnPeriod = tuple.get("returnPeriod", Integer.class);
    this.actualReturnDate = tuple.get("actualReturnDate", Date.class);
    this.status = tuple.get("status", Integer.class);
    this.statusSaving = tuple.get("statusSaving", Integer.class);
    this.note = tuple.get("note", String.class);
//    this.lateMinutes = tuple.get("lateMinutes", Long.class);
    this.createdDate = tuple.get("createdDate", Date.class);
    this.modifiedDate = tuple.get("modifiedDate", Date.class);
  }
}

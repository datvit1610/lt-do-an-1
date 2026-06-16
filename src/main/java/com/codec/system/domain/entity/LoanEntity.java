package com.codec.system.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;

import java.util.Date;

@Getter
@Setter
@Table(name = "loans")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanEntity extends BaseEntity {

  @Comment("Mã phiếu mượn (tự sinh, duy nhất)")
  @Column(name = "loan_code", unique = true)
  String loanCode;

  @Comment("Id người mượn")
  @Column(name = "borrower_id", nullable = false)
  String borrowerId;

  @Comment("Id thiết bị (nếu mượn thiết bị có trong hệ thống)")
  @Column(name = "device_id")
  String deviceId;

  @Comment("Số lượng mượn")
  @Column(name = "quantity")
  Integer quantity;

  @Comment("Thời gian mượn (ngày + giờ)")
  @Column(name = "borrow_date")
  Date borrowDate;

  @Comment("Tiết mượn (số tiết tham chiếu class_periods)")
  @Column(name = "borrow_period")
  Integer borrowPeriod;

  @Comment("Tiết trả (mốc tính hạn trả - giờ kết thúc tiết này)")
  @Column(name = "return_period")
  Integer returnPeriod;

  @Comment("Thời gian trả thực tế (điền khi trả)")
  @Column(name = "actual_return_date")
  Date actualReturnDate;

  @Comment("Trạng thái: 1 - đang mượn, 2 - đã trả, 3 - Trả chậm, 4 - Mất thiết bị")
  @Column(name = "status")
  Integer status;

  @Comment("Ghi chú")
  @Column(name = "note")
  String note;

  @Comment("Số phút trả chậm (tính động theo masterdata tiết + ngưỡng cấu hình)")
  @Column(name = "late_minutes")
  Long lateMinutes;

  @Comment("Trạng thái duyệt: 0 - chưa duyệt (mặc định), 1 - đã duyệt, 2 - hủy")
  @Column(name = "approve_status")
  Integer approveStatus = 0;

  @Comment("Id admin duyệt phiếu (userId)")
  @Column(name = "approved_by")
  String approvedBy;

  @Comment("Thời gian duyệt phiếu")
  @Column(name = "approved_date")
  Date approvedDate;

}

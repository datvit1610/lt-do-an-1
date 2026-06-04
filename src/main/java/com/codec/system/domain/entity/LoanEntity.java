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

  @Comment("Id người mượn")
  @Column(name = "borrower_id", nullable = false)
  String borrowerId;

  @Comment("Id thiết bị (nếu mượn thiết bị có trong hệ thống)")
  @Column(name = "device_id")
  String deviceId;

  @Comment("Tên món đồ mượn")
  @Column(name = "item_name", nullable = false)
  String itemName;

  @Comment("Số lượng mượn")
  @Column(name = "quantity")
  Integer quantity;

  @Comment("Ngày bắt đầu mượn")
  @Column(name = "borrow_date")
  Date borrowDate;

  @Comment("Ngày trả dự kiến")
  @Column(name = "expected_return_date")
  Date expectedReturnDate;

  @Comment("Ngày trả thực tế")
  @Column(name = "actual_return_date")
  Date actualReturnDate;

  @Comment("Trạng thái: 1 - đang mượn, 2 - đã trả")
  @Column(name = "status")
  Integer status;

  @Comment("Ghi chú")
  @Column(name = "note")
  String note;

}

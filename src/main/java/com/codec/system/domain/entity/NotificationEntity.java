package com.codec.system.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Table(name = "notifications")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEntity extends BaseEntity {

  @Comment("Tiêu đề thông báo")
  @Column(name = "title")
  String title;

  @Comment("Nội dung thông báo")
  @Column(name = "message", columnDefinition = "TEXT")
  String message;

  @Comment("Loại thông báo: LATE_RETURN - trả chậm, LOST - mất thiết bị")
  @Column(name = "type")
  String type;

  @Comment("Id tham chiếu (loan_id)")
  @Column(name = "ref_id")
  String refId;

  @Comment("Id người nhận thông báo (borrower_id - id trong bảng users)")
  @Column(name = "user_id")
  String userId;

  @Comment("Đã đọc hay chưa")
  @Column(name = "is_read", columnDefinition = "BOOLEAN DEFAULT false")
  Boolean isRead = false;

}

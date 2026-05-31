package com.codec.system.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

// Log thông báo đã gửi
@Getter
@Setter
@Entity
@Table(name = "notification_log")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationLogEntity extends BaseEntity {

  @Column(name = "content", columnDefinition = "TEXT")
  String content;

  @Comment("Số sản phẩm sắp hết hạn tại thời điểm quét")
  @Column(name = "near_expiry_count")
  Integer nearExpiryCount;

  @Comment("Thời điểm gửi thực tế, null = chưa gửi")
  @Column(name = "sent_at")
  LocalDateTime sentAt;

  @Column(name = "success_count")
  Integer successCount;

  @Column(name = "fail_count")
  Integer failCount;
}

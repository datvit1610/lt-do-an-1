package com.codec.system.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "notification_user")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationUserEntity extends BaseEntity {

  @Column(name = "notification_log_id", nullable = false)
  String notificationLogId;

  @Column(name = "user_id", nullable = false)
  String userId;

  @Comment("true: đã đọc, false: chưa đọc")
  @Column(name = "is_read")
  Boolean isRead = false;

  @Comment("Thời điểm đọc")
  @Column(name = "read_at")
  LocalDateTime readAt;
}

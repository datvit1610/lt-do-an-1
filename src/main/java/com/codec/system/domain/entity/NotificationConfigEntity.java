package com.codec.system.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Table(name = "notification_config")  //danh sách nhóm quyền
@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationConfigEntity extends BaseEntity {

  @Comment("Ngày báo hết hạn")
  @Column(name = "expired_day_notify", nullable = false)
  Integer expiredDayNotify;

  @Comment("Giờ bắn thông báo, mặc định 6")
  @Column(name = "notify_hour")
  Integer notifyHour = 6;

  @Comment("Phút bắn thông báo, mặc định 0")
  @Column(name = "notify_minute")
  Integer notifyMinute = 0;
}

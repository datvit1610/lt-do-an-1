package com.codec.system.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;

// Lưu FCM token thiết bị của user
@Getter
@Setter
@Entity
@Table(name = "user_device")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDeviceEntity extends BaseEntity {

  @Column(name = "user_id", nullable = false)
  String userId;

  @Column(name = "fcm_token", nullable = false, length = 500)
  String fcmToken;

  @Comment("Android / iOS")
  @Column(name = "platform")
  String platform;

  @Comment("1: active, 0: inactive")
  @Column(name = "is_active")
  Boolean isActive = true;
}

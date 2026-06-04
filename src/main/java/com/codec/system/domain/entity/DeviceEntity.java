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
@Table(name = "devices")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceEntity extends BaseEntity {

  @Comment("Tên thiết bị")
  @Column(name = "name", nullable = false)
  String name;

  @Comment("Số serial của thiết bị")
  @Column(name = "serial_number", unique = true)
  String serialNumber;

  @Comment("Loại thiết bị")
  @Column(name = "device_type")
  String deviceType;

  @Comment("1: hoạt động, 0: ngưng")
  @Column(name = "status")
  Integer status;

  @Comment("Vị trí/địa điểm thiết bị")
  @Column(name = "location")
  String location;

  @Comment("Id người được gán thiết bị")
  @Column(name = "assigned_user_id")
  String assignedUserId;

  @Comment("Mô tả thiết bị")
  @Column(name = "description")
  String description;

}

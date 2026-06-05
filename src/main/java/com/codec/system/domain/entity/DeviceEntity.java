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

  @Comment("Mã thiết bị")
  @Column(name = "device_code", unique = true)
  String deviceCode;

  @Comment("Loại thiết bị")
  @Column(name = "device_type")
  String deviceType;

  @Comment("1: hoạt động, 0: ngưng")
  @Column(name = "status")
  Integer status;

  @Comment("Vị trí/địa điểm thiết bị")
  @Column(name = "location")
  String location;

  @Comment("Số lượng")
  @Column(name = "quantity")
  Integer quantity;

  @Comment("Mô tả thiết bị")
  @Column(name = "description")
  String description;

}

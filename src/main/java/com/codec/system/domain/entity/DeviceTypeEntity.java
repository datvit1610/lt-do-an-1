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
@Table(name = "device_type")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceTypeEntity extends BaseEntity {

  @Comment("Tên loại thiết bị")
  @Column(name = "device_type", nullable = false)
  String deviceType;

}

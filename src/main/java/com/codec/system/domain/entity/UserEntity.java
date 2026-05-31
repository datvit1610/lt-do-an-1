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
@Table(name = "users")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity extends BaseEntity {

  @Column(name = "username", nullable = false)
  String username;

  @Column(name = "password", nullable = false)
  String password;

  @Column(name = "full_name", nullable = false)
  String fullName;

  @Column(name = "phone_number", nullable = false)
  String phoneNumber;

  @Column(name = "email")
  String email;

  @Comment("Chức vụ")
  @Column(name = "position")
  String position;

  @Comment("1: admin, 2: nhân viên")
  @Column(name = "account_type")
  Integer accountType; //1: admin, 2: nhân viên

  @Comment("1: hoạt động, 2: tạm ngưng")
  @Column(name = "status", nullable = false)
  Integer status; //1: hoạt động, 0: tạm ngưng

  @Column(name = "role_id", nullable = false)
  String roleId;
}

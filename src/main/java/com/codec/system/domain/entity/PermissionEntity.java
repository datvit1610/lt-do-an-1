package com.codec.system.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Table(name = "permission")  //danh sách mã quyền
@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionEntity extends BaseEntity {

  @Comment("Tên quyền")
  @Column(name = "name", nullable = false)
  String name;

  @Comment("Mô tả")
  @Column(name = "description", nullable = false)
  String description;

  @Comment("Nhóm quyền")
  @Column(name = "group_id")
  Integer groupId;
}

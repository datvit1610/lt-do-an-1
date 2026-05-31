package com.codec.system.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Table(name = "role_permission")  //danh sách quyền theo nhóm quyền
@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RolePermissionEntity extends BaseEntity {

  @Comment("id nhóm quyền")
  @Column(name = "role_id", nullable = false)
  String roleId;

  @Comment("id quyền")
  @Column(name = "permission_id", nullable = false)
  String permissionId;
}

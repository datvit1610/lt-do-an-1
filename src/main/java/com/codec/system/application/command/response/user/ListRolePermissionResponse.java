package com.codec.system.application.command.response.user;

import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListRolePermissionResponse {
  String roleId;
  String roleName;
  List<Permission> permissions;

  public ListRolePermissionResponse(Tuple tuple) {
    this.roleId = tuple.get("roleId", String.class);
    this.roleName = tuple.get("roleName", String.class);
  }

  @Data
  public static class Permission {
    String roleId;
    String permissionId;
    String permissionName;
    String description;

    public Permission(Tuple tuple) {
      this.roleId = tuple.get("roleId", String.class);
      this.permissionId = tuple.get("permissionId", String.class);
      this.permissionName = tuple.get("permissionName", String.class);
      this.description = tuple.get("description", String.class);
    }

  }
}

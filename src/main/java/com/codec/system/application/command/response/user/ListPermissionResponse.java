package com.codec.system.application.command.response.user;

import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListPermissionResponse {
    String permissionId;
    String permissionName;
    String description;
    Integer groupId;

    public ListPermissionResponse(Tuple tuple) {
      this.permissionId = tuple.get("permissionId", String.class);
      this.permissionName = tuple.get("permissionName", String.class);
      this.description = tuple.get("description", String.class);
      this.groupId = tuple.get("groupId", Integer.class);
    }
}

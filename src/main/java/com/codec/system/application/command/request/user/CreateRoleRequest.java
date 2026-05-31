package com.codec.system.application.command.request.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRoleRequest {
  String roleName;
  List<String> permissionOlds;
  List<String> permissionNews;
}

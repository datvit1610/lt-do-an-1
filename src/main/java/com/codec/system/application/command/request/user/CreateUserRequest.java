package com.codec.system.application.command.request.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {
  String userName;
  String password;
  String fullName;
  String email;
  String phoneNumber;
  String position;
  Integer status;
  String roleId;
}

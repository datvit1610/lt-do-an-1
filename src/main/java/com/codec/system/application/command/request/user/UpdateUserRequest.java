package com.codec.system.application.command.request.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {
  String password;
  String fullName;
  String email;
  String phoneNumber;
  String position;
  String address;
  Integer status;
  String roleId;
  List<String> channelOlds;
  List<String> channelNews;
}

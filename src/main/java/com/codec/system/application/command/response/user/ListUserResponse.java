package com.codec.system.application.command.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListUserResponse {
  String userId;
  String roleId;
  String roleName;
  String userName;
  String fullName;
  String email;
  String phoneNumber;
  String position;
  Integer status;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
  Date createdDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
  Date modifiedDate;
  String createdBy;

  public ListUserResponse(Tuple tuple) {
    this.userId = tuple.get("userId", String.class);
    this.roleId = tuple.get("roleId", String.class);
    this.roleName = tuple.get("roleName", String.class);
    this.userName = tuple.get("userName", String.class);
    this.fullName = tuple.get("fullName", String.class);
    this.email = tuple.get("email", String.class);
    this.phoneNumber = tuple.get("phoneNumber", String.class);
    this.position = tuple.get("position", String.class);
    this.status = tuple.get("status", Integer.class);
    this.createdDate = tuple.get("createdDate", Date.class);
    this.modifiedDate = tuple.get("modifiedDate", Date.class);
    this.createdBy = tuple.get("createdBy", String.class);
  }

}

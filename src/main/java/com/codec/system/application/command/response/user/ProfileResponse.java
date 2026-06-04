package com.codec.system.application.command.response.user;

import jakarta.persistence.Tuple;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
  String userId;
  String username;
  String phoneNumber;
  String fullName;
  Integer status;
  RoleInfo roleInfo;
  String email;
  String position;

  public ProfileResponse(String userId , String username,  String phoneNumber, String fullName, Integer status,  String email, String position) {
    this.userId = userId;
    this.username = username;
    this.phoneNumber = phoneNumber;
    this.fullName = fullName;
    this.status = status;
    this.email = email;
    this.position = position;
  }

  @Data
  public static class RoleInfo {
    String id;
    String name;
    List<Permission> permissions;

    @Data
    public static class Permission {
      String id;
      String name;
      String description;

      public Permission(Tuple tuple) {
        this.id = tuple.get("permissionId", String.class);
        this.name = tuple.get("permissionName", String.class);
        this.description = tuple.get("description", String.class);
      }
    }
  }
}

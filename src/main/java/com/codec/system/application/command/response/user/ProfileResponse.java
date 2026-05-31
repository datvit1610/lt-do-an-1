package com.codec.system.application.command.response.user;

import jakarta.persistence.Tuple;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
  String userId;
  String phoneNumber;
  String fullName;
  Integer status;
  Integer accountType;
  PublisherInfo publisherInfo;
  RoleInfo roleInfo;

  public ProfileResponse(String userId , String phoneNumber, String fullName, Integer status, Integer accountType) {
    this.userId = userId;
    this.phoneNumber = phoneNumber;
    this.fullName = fullName;
    this.status = status;
    this.accountType = accountType;
  }

  @Data
  public static class PublisherInfo {
    String guid;
    String name;
    String email;
    String phoneNumber;
    String contractNumber;
    String address;
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

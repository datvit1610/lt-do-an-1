package com.codec.system.application.command.response.device;

import lombok.Data;

import java.util.Date;

@Data
public class DeviceResponse {
  private String id;
  private String name;
  private String deviceCode;
  private String deviceType;
  private Integer status;
  private String location;
  private Integer quantity;
  private String description;
  private Date createdDate;
  private Date modifiedDate;
}

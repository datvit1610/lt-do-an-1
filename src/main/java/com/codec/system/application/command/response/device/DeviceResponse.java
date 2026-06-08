package com.codec.system.application.command.response.device;

import com.fasterxml.jackson.annotation.JsonFormat;
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
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
  private Date createdDate;
  private String createdBy; // tên người tạo (join từ bảng users)
}

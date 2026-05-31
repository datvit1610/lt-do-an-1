package com.codec.system.application.command.request.warehouse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BulkUpdateWarehouseRequest {

  @NotEmpty(message = "Danh sách sản phẩm không được trống")
  private List<String> ids;

  // Cột cần update
  @NotBlank(message = "Tên cột không được trống")
  private String fieldName;

  // Giá trị mới
  private String value;
}

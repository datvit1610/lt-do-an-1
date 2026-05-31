package com.codec.system.application.command.request.warehouse;

import lombok.Data;

@Data
public class ExportProductRequest {
  // id sản phẩm kho
  private String warehouseId;

  // số lượng muốn xuất
  private Integer exportQuantity;
}

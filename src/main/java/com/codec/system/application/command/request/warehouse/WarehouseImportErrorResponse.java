package com.codec.system.application.command.request.warehouse;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WarehouseImportErrorResponse {
  //nhóm sản phẩm
  private String productGroup;

  //tên sản phẩm
  private String productName;

  //xuất xứ
  private String origin;

  //chi tiết sản phẩm
  private String productDetail;

  // Quy cách pha chế
  private String mixingSpecification;

  // Ngày sản xuất
  private String manufacturingDate;

  // Ngày hết hạn
  private String expiryDate;

  // Ngày nhập kho
  private String importDate;

  // Ngày hết hạn
  private String unit;

  // Số lượng nhập
  private Integer importQuantity;

  // Người nhập kho
  private String importedBy;

  // Vị trí bảo quản
  private String storageLocation;

  // Ghi chú
  private String note;

  //gh chú lỗi
  private String errorNote;
}

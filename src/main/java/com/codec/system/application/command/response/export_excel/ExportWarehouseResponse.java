package com.codec.system.application.command.response.export_excel;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExportWarehouseResponse {
  private Integer stt;

  // Nhóm sản phẩm
  private String productGroup;

  // Tên sản phẩm
  private String productName;

  // Nguồn gốc/Xuất xứ
  private String origin;

  // Chi tiết sản phẩm
  private String productDetail;

  // Quy cách pha chế
  private String mixingSpecification;

  // Ngày sản xuất
  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate manufacturingDate;

  // Ngày hết hạn
  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate expiryDate;

  // Ngày nhập kho
  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate importDate;

  // ĐVT
  private String unit;

  // Số lượng nhập
  private Integer importQuantity;

  // Người nhập kho
  private String importedBy;

  // Vị trí bảo quản
  private String storageLocation;

  // Ghi chú
  private String note;

  //cột số ngày còn hạn
  private String daysUntilExpiry;

  private String status; // 1: Còn hạn, 2: sắp hết hạn 3: Hết hạn

  private Integer exportQuantity;    // Số lượng đã xuất
  private Integer remainingQuantity; // Số lượng còn lại
  private String stockStatus;       // 1: còn hàng, 2: hết hàng
}

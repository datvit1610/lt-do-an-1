package com.codec.system.application.command.response.warehouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListWarehouseResponse {
  private String id;

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

  private Integer status; // 1: Còn hạn, 2: sắp hết hạn 3: Hết hạn 4: Vô thời hạn

  //qrcode
  private String qrCode;
  private Integer exportQuantity;    // Số lượng đã xuất
  private Integer remainingQuantity; // Số lượng còn lại
  private Integer stockStatus;       // 1: còn hàng, 2: hết hàng

  public ListWarehouseResponse (Tuple tuple) {
    this.id = tuple.get("id", String.class);
    this.productGroup = tuple.get("productGroup", String.class);
    this.productName = tuple.get("productName", String.class);
    this.origin = tuple.get("origin", String.class);
    this.productDetail = tuple.get("productDetail", String.class);
    this.mixingSpecification = tuple.get("mixingSpecification", String.class);
    this.manufacturingDate = tuple.get("manufacturingDate", LocalDate.class);
    this.expiryDate = tuple.get("expiryDate", LocalDate.class);
    this.importDate = tuple.get("importDate", LocalDate.class);
    this.unit = tuple.get("unit", String.class);
    this.importQuantity = tuple.get("importQuantity", Integer.class);
    this.importedBy = tuple.get("importedBy", String.class);
    this.storageLocation = tuple.get("storageLocation", String.class);
    this.note = tuple.get("note", String.class);
    this.qrCode = tuple.get("qrCode", String.class);
    this.status = tuple.get("status", Integer.class);
    this.exportQuantity = tuple.get("exportQuantity", Integer.class);
    this.remainingQuantity = tuple.get("remainingQuantity", Integer.class);
    this.stockStatus = tuple.get("stockStatus", Integer.class);
  }
}

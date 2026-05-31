package com.codec.system.application.command.response.qrcode;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DetailWarehouseResponse {

  String id;

  // Nhóm sản phẩm
  String productGroup;

  // Tên sản phẩm
  String productName;

  // Nguồn gốc/Xuất xứ
  String origin;

  // Chi tiết sản phẩm
  String productDetail;

  // Quy cách pha chế
  String mixingSpecification;

  // Ngày sản xuất
  @JsonFormat(pattern = "dd/MM/yyyy")
  LocalDate manufacturingDate;

  // Ngày hết hạn
  @JsonFormat(pattern = "dd/MM/yyyy")
  LocalDate expiryDate;

  // Ngày nhập kho
  @JsonFormat(pattern = "dd/MM/yyyy")
  LocalDate importDate;

  // ĐVT
  String unit;

  // Số lượng nhập
  Integer importQuantity;

  // Người nhập kho
  String importedBy;

  // Vị trí bảo quản
  String storageLocation;

  // Ghi chú
  String note;

  // QR code base64
  String qrCode;
}

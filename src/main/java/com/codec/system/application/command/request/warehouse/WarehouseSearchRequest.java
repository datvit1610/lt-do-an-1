package com.codec.system.application.command.request.warehouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WarehouseSearchRequest {
  private String productGroup;
  private String productName;
  private String origin;
  private String productDetail;
  private String storageLocation;
  private Integer status; // 1: Còn hạn, 2: sắp hết hạn 3: Hết hạn
  // 1: còn hàng, 2: hết hàng
  private Integer stockStatus;
  //trạng thái QR
  private Integer qrStatus; // 1: chưa quét, 2: đã quét
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate importDateFrom;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate importDateTo;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate expiryDateFrom;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate expiryDateTo;
}

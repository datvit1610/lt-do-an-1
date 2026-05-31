package com.codec.system.domain.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Getter
@Setter
@Table(name = "warehouse") //bảng kho hàng
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseEntity extends BaseEntity{
  // Nhóm sản phẩm
  @Column(name = "product_group")
  private String productGroup;

  // Tên sản phẩm
  @Column(name = "product_name")
  private String productName;

  // Nguồn gốc/Xuất xứ
  @Column(name = "origin")
  private String origin;

  // Chi tiết sản phẩm
  @Column(name = "product_detail", columnDefinition = "TEXT")
  private String productDetail;

  // Quy cách pha chế
  @Column(name = "mixing_specification")
  private String mixingSpecification;

  // Ngày sản xuất
  @Column(name = "manufacturing_date")
  private LocalDate manufacturingDate;

  // Ngày hết hạn
  @Column(name = "expiry_date")
  private LocalDate expiryDate;

  // Ngày nhập kho
  @Column(name = "import_date")
  private LocalDate importDate;

  // ĐVT
  @Column(name = "unit")
  private String unit;

  // Số lượng nhập
  @Column(name = "import_quantity")
  private Integer importQuantity;

  // Người nhập kho
  @Column(name = "imported_by")
  private String importedBy;

  // Vị trí bảo quản
  @Column(name = "storage_location")
  private String storageLocation;

  // Ghi chú
  @Column(name = "note", columnDefinition = "TEXT")
  private String note;

  @Comment("QR code base64")
  @Column(name = "qr_code", columnDefinition = "TEXT")
  private String qrCode;

  @Comment("id lô hàng")
  @Column(name = "batch_id")
  private String batchId;

  @Comment("Số lượng đã xuất")
  @Column(name = "export_quantity")
  private Integer exportQuantity = 0;
}

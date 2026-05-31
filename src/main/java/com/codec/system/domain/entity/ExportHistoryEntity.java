package com.codec.system.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Entity
@Table(name = "export_history")
@Getter
@Setter
public class ExportHistoryEntity {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "warehouse_id")
  private String warehouseId;

  // số lượng xuất
  @Column(name = "export_quantity")
  private Integer exportQuantity;

  // thời gian xuất
  @Column(name = "export_date")
  private Date exportDate;

  // người xuất
  @Column(name = "exported_by")
  private String exportedBy;
}

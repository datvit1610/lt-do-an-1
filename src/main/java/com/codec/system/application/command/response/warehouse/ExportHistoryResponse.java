package com.codec.system.application.command.response.warehouse;

import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportHistoryResponse {

  private String historyId;

  private String warehouseId;

  private Date exportDate;

  private String productGroup;

  private String productName;

  private String productDetail;

  private String mixingSpecification;

  private LocalDate importDate;

  private String unit;

  private Integer exportQuantity;

  private String exportedBy;

  private LocalDate expiryDate;

  private String storageLocation;

  private String origin;

  public ExportHistoryResponse(Tuple tuple) {

    this.historyId =
      tuple.get("historyId", String.class);

    this.warehouseId =
      tuple.get("warehouseId", String.class);

    this.exportDate =
      tuple.get("exportDate", Date.class);

    this.productGroup =
      tuple.get("productGroup", String.class);

    this.productName =
      tuple.get("productName", String.class);

    this.productDetail =
      tuple.get("productDetail", String.class);

    this.mixingSpecification =
      tuple.get("mixingSpecification", String.class);

    this.importDate =
      tuple.get("importDate", LocalDate.class);

    this.unit =
      tuple.get("unit", String.class);

    this.exportQuantity =
      tuple.get("exportQuantity", Integer.class);

    this.exportedBy =
      tuple.get("exportedBy", String.class);

    this.expiryDate =
      tuple.get("expiryDate", LocalDate.class);

    this.storageLocation =
      tuple.get("storageLocation", String.class);

    this.origin =
      tuple.get("origin", String.class);
  }

}

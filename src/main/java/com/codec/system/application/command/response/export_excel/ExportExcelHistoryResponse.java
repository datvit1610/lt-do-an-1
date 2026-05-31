package com.codec.system.application.command.response.export_excel;

import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ExportExcelHistoryResponse {

  private Integer stt;

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

}

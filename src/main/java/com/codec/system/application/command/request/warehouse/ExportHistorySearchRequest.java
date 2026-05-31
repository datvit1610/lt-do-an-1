package com.codec.system.application.command.request.warehouse;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExportHistorySearchRequest {
  private LocalDate exportDateFrom;

  private LocalDate exportDateTo;

  private String productGroup;

  private String productName;

  private String exportedBy;

  private String storageLocation;
}

package com.codec.system.application.service;

import codec.common.Response;
import com.codec.system.application.command.request.warehouse.*;
import com.codec.system.application.command.response.warehouse.ExportHistoryResponse;
import com.codec.system.application.command.response.warehouse.ListWarehouseResponse;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseInventoryService {
  Response<RestCodecSystemApplicationPage<ListWarehouseResponse>> searchWarehouse(
    WarehouseSearchRequest request, Pageable pageable);
  Response<RestCodecSystemApplicationPage<ListWarehouseResponse>> searchForExportWarehouse(
    WarehouseSearchRequest request, Pageable pageable);
  byte[] exportWarehouse(WarehouseSearchRequest request);
  void updateWarehouse(String id,
                       CreateWarehouseRequest request,
                       String userId);
  void deleteWarehouse(String id, String userId);
  void exportWarehouse(List<ExportProductRequest> request,
                       String userId);
  Response<RestCodecSystemApplicationPage<ExportHistoryResponse>> searchExportHistory(ExportHistorySearchRequest request, Pageable pageable);
  byte[] exportHistory(ExportHistorySearchRequest request);

  Response<Void> bulkUpdate(BulkUpdateWarehouseRequest request, String userId);
}

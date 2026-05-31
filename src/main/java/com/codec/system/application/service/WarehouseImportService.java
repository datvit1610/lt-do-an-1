package com.codec.system.application.service;

import codec.common.Response;
import com.codec.system.application.command.request.warehouse.CreateWarehouseRequest;
import com.codec.system.application.command.request.warehouse.WarehouseImportErrorResponse;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.List;

public interface WarehouseImportService {
  void createWarehouse(CreateWarehouseRequest createWarehouseRequest, String userId);
  Mono<Response<List<WarehouseImportErrorResponse>>> importWarehouseFromExcel(
    FilePart file, String userId);
}

package com.codec.system.api;

import codec.common.Response;
import com.codec.system.application.command.request.qrcode.GenerateQRRequest;
import com.codec.system.application.command.request.warehouse.*;
import com.codec.system.application.command.response.qrcode.DetailWarehouseResponse;
import com.codec.system.application.command.response.qrcode.QRGenerateResponse;
import com.codec.system.application.command.response.warehouse.ExportHistoryResponse;
import com.codec.system.application.command.response.warehouse.ListWarehouseResponse;
import com.codec.system.application.service.WarehouseImportService;
import com.codec.system.application.service.WarehouseInventoryService;
import com.codec.system.application.service.WarehouseQRService;
import com.codec.system.application.service.authen.JwtUtil;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequestMapping("/api/v1")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WarehouseController {
  WarehouseImportService warehouseImportService;
  WarehouseInventoryService warehouseInventoryService;
  WarehouseQRService warehouseQRService;
  JwtUtil jwtUtil;

  @Operation(summary = "Thêm mới nhập kho thủ công")
  @PostMapping("/warehouse/create")
  public Mono<Response<Object>> createWarehouse(
    @RequestBody CreateWarehouseRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "warehouse-c");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    warehouseImportService.createWarehouse(request, userId);
    return Mono.just(Response.ok().success("Thêm mới thành công", 201));
  }


  @Comment("Nhập kho hàng loạt từ file Excel")
  @PostMapping(value = "/warehouse/import", consumes = {"multipart/form-data"})
  public Mono<Response<List<WarehouseImportErrorResponse>>> importWarehouseFromExcel(
    @RequestPart("file") FilePart file,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "warehouse-import");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    return warehouseImportService.importWarehouseFromExcel(file, userId);
  }


  @Comment("Danh sách kho hàng")
  @GetMapping("/warehouse/search")
  public Mono<Response<RestCodecSystemApplicationPage<ListWarehouseResponse>>> searchWarehouse(
    @RequestParam(required = false) String productGroup,
    @RequestParam(required = false) String productName,
    @RequestParam(required = false) String origin,
    @RequestParam(required = false) String productDetail,
    @RequestParam(required = false) String storageLocation,
    @RequestParam(required = false) Integer status, // 1: Còn hạn, 2: sắp hết hạn 3: Hết hạn 4: Vô thời hạn
    @RequestParam(required = false) Integer stockStatus, // 1: còn hàng, 2: hết hàng
    @RequestParam(required = false) Integer qrStatus, // 1: chưa quét, 2: đã quét
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate importDateFrom,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate importDateTo,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDateFrom,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDateTo,
    @ParameterObject Pageable pageable,
    @RequestHeader("Authorization") String authHeader) {
    String userId = jwtUtil.checkPermission(authHeader, "product-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }

    WarehouseSearchRequest request = new WarehouseSearchRequest();
    request.setProductGroup(productGroup);
    request.setProductName(productName);
    request.setOrigin(origin);
    request.setProductDetail(productDetail);
    request.setStorageLocation(storageLocation);
    request.setImportDateFrom(importDateFrom);
    request.setImportDateTo(importDateTo);
    request.setExpiryDateFrom(expiryDateFrom);
    request.setExpiryDateTo(expiryDateTo);
    request.setStatus(status);
    request.setStockStatus(stockStatus);
    request.setQrStatus(qrStatus);

    RestCodecSystemApplicationPage<ListWarehouseResponse> response = warehouseInventoryService.searchWarehouse(request, pageable).getData();
    return Mono.just(Response.of(response).success("Thành công", 200));
  }

  @Comment("Danh sách kho hàng")
  @GetMapping("/warehouse/search-for-export")
  public Mono<Response<RestCodecSystemApplicationPage<ListWarehouseResponse>>> searchWarehouseForExport(
    @RequestParam(required = false) String productGroup,
    @RequestParam(required = false) String productName,
    @RequestParam(required = false) String origin,
    @RequestParam(required = false) String storageLocation,
    @ParameterObject Pageable pageable,
    @RequestHeader("Authorization") String authHeader) {
    String userId = jwtUtil.checkPermission(authHeader, "product-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }

    WarehouseSearchRequest request = new WarehouseSearchRequest();
    request.setProductGroup(productGroup);
    request.setProductName(productName);
    request.setOrigin(origin);
    request.setStorageLocation(storageLocation);

    RestCodecSystemApplicationPage<ListWarehouseResponse> response = warehouseInventoryService.searchForExportWarehouse(request, pageable).getData();
    return Mono.just(Response.of(response).success("Thành công", 200));
  }

  @GetMapping("/warehouse/search/export")
  public Mono<ResponseEntity<byte[]>> exportCampaignOtt(
    @RequestParam(required = false) String productGroup,
    @RequestParam(required = false) String productName,
    @RequestParam(required = false) String origin,
    @RequestParam(required = false) String productDetail,
    @RequestParam(required = false) String storageLocation,
    @RequestParam(required = false) Integer status, // 1: Còn hạn, 2: sắp hết hạn 3: Hết hạn
    @RequestParam(required = false) Integer stockStatus, // 1: còn hàng, 2: hết hàng
    @RequestParam(required = false) Integer qrStatus, // 1: chưa quét, 2: đã quét
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate importDateFrom,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate importDateTo,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDateFrom,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDateTo,
    @RequestHeader("Authorization") String authHeader) {
    String userId = jwtUtil.checkPermission(authHeader, "product-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(ResponseEntity.status(403).build());
    }

    WarehouseSearchRequest request = new WarehouseSearchRequest();
    request.setProductGroup(productGroup);
    request.setProductName(productName);
    request.setOrigin(origin);
    request.setProductDetail(productDetail);
    request.setStorageLocation(storageLocation);
    request.setImportDateFrom(importDateFrom);
    request.setImportDateTo(importDateTo);
    request.setExpiryDateFrom(expiryDateFrom);
    request.setExpiryDateTo(expiryDateTo);
    request.setStatus(status);
    request.setStockStatus(stockStatus);
    request.setQrStatus(qrStatus);

    byte[] file = warehouseInventoryService.exportWarehouse(request);

    String fileName = "danh-sach-xuat-kho_" +
      LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
      ".xlsx";

    return Mono.just(
      ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=\"" + fileName + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(file)
    );
  }

  @Comment("Cập nhật thông tin kho hàng")
  @PostMapping("/warehouse/update/{id}")
  public Mono<Response<Object>> updateWarehouse(
    @PathVariable String id,
    @RequestBody CreateWarehouseRequest request,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "product-u");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    warehouseInventoryService.updateWarehouse(id, request, userId);
    return Mono.just(Response.ok().success("Cập nhật thành công", 201));
  }

  @Comment("Cập nhật thông tin kho hàng")
  @PostMapping("/warehouse/delete/{id}")
  public Mono<Response<Object>> deleteWarehouse(
    @PathVariable String id,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "product-d");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    warehouseInventoryService.deleteWarehouse(id, userId);
    return Mono.just(Response.ok().success("Xóa thành công", 201));
  }

  @Comment("Xuất kho hàng")
  @PostMapping("/warehouse/export")
  public Mono<Response<Object>> exportWarehouse(
    @RequestBody List<ExportProductRequest> request,
    @RequestHeader("Authorization") String authHeader
  ) {

    String userId = jwtUtil.checkPermission(authHeader, "product-c");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    warehouseInventoryService.exportWarehouse(request, userId);
    return Mono.just(Response.ok().success("Xuất kho thành công", 201));
  }

  @GetMapping("/warehouse/export-history")
  public Mono<Response<RestCodecSystemApplicationPage<ExportHistoryResponse>>> searchExportHistory(
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate exportDateFrom,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate exportDateTo,
    @RequestParam(required = false) String productGroup,
    @RequestParam(required = false) String productName,
    @RequestParam(required = false) String exportedBy,
    @RequestParam(required = false) String storageLocation,
    @ParameterObject Pageable pageable
  ) {
    ExportHistorySearchRequest request = new ExportHistorySearchRequest();
    request.setExportDateFrom(exportDateFrom);
    request.setExportDateTo(exportDateTo);
    request.setProductGroup(productGroup);
    request.setProductName(productName);
    request.setExportedBy(exportedBy);
    request.setStorageLocation(storageLocation);
    RestCodecSystemApplicationPage<ExportHistoryResponse> response = warehouseInventoryService.searchExportHistory(request, pageable).getData();
    return Mono.just(Response.of(response).success("Thành công", 200)
    );
  }

  @GetMapping("/warehouse/export-history/export")
  public Mono<ResponseEntity<byte[]>> exportHistory(
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate exportDateFrom,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate exportDateTo,
    @RequestParam(required = false) String productGroup,
    @RequestParam(required = false) String productName,
    @RequestParam(required = false) String exportedBy,
    @RequestParam(required = false) String storageLocation
  ) {
    ExportHistorySearchRequest request = new ExportHistorySearchRequest();
    request.setExportDateFrom(exportDateFrom);
    request.setExportDateTo(exportDateTo);
    request.setProductGroup(productGroup);
    request.setProductName(productName);
    request.setExportedBy(exportedBy);
    request.setStorageLocation(storageLocation);
    byte[] file = warehouseInventoryService.exportHistory(request);

    String fileName = "danh-sach-xuat-kho_" +
      LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
      ".xlsx";

    return Mono.just(
      ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
          "attachment; filename=\"" + fileName + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(file)
    );
  }

  @PostMapping("/warehouse/generate-qr")
  public Response<List<QRGenerateResponse>> generateQR(
    @RequestBody @Valid GenerateQRRequest request) {
    return warehouseQRService.generateQR(request);
  }

  // API này không cần auth — FE quét QR gọi thẳng
  @GetMapping("/warehouse/detail/{id}")
  public Response<DetailWarehouseResponse> getDetailByQR(@PathVariable String id) {
    return warehouseQRService.getDetailByQR(id);
  }

  @Comment("Cập nhật hàng loạt thông tin kho hàng")
  @PostMapping("/warehouse/bulk-update")
  public Response<Void> bulkUpdate(@RequestBody @Valid BulkUpdateWarehouseRequest request,
                                   @RequestHeader("Authorization") String authHeader) {
    String userId = jwtUtil.getUserId(authHeader);
    return warehouseInventoryService.bulkUpdate(request, userId);
  }
}

package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.application.command.request.task_history.TaskHistoryRequest;
import com.codec.system.application.command.request.warehouse.CreateWarehouseRequest;
import com.codec.system.application.command.request.warehouse.WarehouseImportErrorResponse;
import com.codec.system.application.service.TaskHistoryService;
import com.codec.system.application.service.WarehouseImportService;
import com.codec.system.domain.entity.WarehouseEntity;
import com.codec.system.domain.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WarehouseImportServiceImpl implements WarehouseImportService {
  private final WarehouseRepository warehouseRepository;
  private final TaskHistoryService taskHistoryService;
  private final TransactionTemplate transactionTemplate;

  /*
  hàm quản tạo phiếu nhập kho
   */
  @Override
  @Transactional
  public void createWarehouse(CreateWarehouseRequest createWarehouseRequest, String userId) {
    //check số lượng phải lớn hơn 0
    if (createWarehouseRequest.getImportQuantity() == null
      || createWarehouseRequest.getImportQuantity() <= 0) {

      throw new RuntimeException("Số lượng nhập phải lớn hơn 0");
    }
    // Check ngày hết hạn phải lớn hơn ngày hiện tại
    if (createWarehouseRequest.getExpiryDate() != null
      && createWarehouseRequest.getExpiryDate().isBefore(LocalDate.now())) {

      throw new RuntimeException("Ngày hết hạn phải lớn hơn ngày hiện tại");
    }

    // Check ngày sản xuất phải nhỏ hơn ngày hết hạn
    if (createWarehouseRequest.getManufacturingDate() != null
      && createWarehouseRequest.getExpiryDate() != null
      && createWarehouseRequest.getManufacturingDate()
      .isAfter(createWarehouseRequest.getExpiryDate())) {

      throw new RuntimeException("Ngày sản xuất phải nhỏ hơn ngày hết hạn");
    }

    //tìm xem đã có sản phẩm vào có trùng tên và trùng đơn vị tính trong 1 nhóm hàng chưa, nếu có rồi thì không cho tạo mới
    boolean exists = warehouseRepository
      .existsByProductGroupAndProductNameAndUnitAndManufacturingDateAndExpiryDateAndDeletedFalse(
        createWarehouseRequest.getProductGroup().trim(),
        createWarehouseRequest.getProductName().trim(),
        createWarehouseRequest.getUnit().trim(),
        createWarehouseRequest.getManufacturingDate(),
        createWarehouseRequest.getExpiryDate()
      );

    if (exists) {
      throw new RuntimeException(
        "Sản phẩm đã tồn tại trong nhóm hàng với cùng đơn vị tính"
      );
    }

    WarehouseEntity warehouseEntity = new WarehouseEntity();
    warehouseEntity.setProductGroup(createWarehouseRequest.getProductGroup());
    warehouseEntity.setProductName(createWarehouseRequest.getProductName());
    warehouseEntity.setOrigin(createWarehouseRequest.getOrigin());
    warehouseEntity.setProductDetail(createWarehouseRequest.getProductDetail());
    warehouseEntity.setMixingSpecification(createWarehouseRequest.getMixingSpecification());
    warehouseEntity.setManufacturingDate(createWarehouseRequest.getManufacturingDate());
    warehouseEntity.setExpiryDate(createWarehouseRequest.getExpiryDate());
    warehouseEntity.setImportDate(createWarehouseRequest.getImportDate());
    warehouseEntity.setUnit(createWarehouseRequest.getUnit());
    warehouseEntity.setImportQuantity(createWarehouseRequest.getImportQuantity());
    warehouseEntity.setImportedBy(createWarehouseRequest.getImportedBy());
    warehouseEntity.setStorageLocation(createWarehouseRequest.getStorageLocation());
    warehouseEntity.setNote(createWarehouseRequest.getNote());
    warehouseEntity.setCreatedBy(userId);
    warehouseEntity.setBatchId(LocalDateTime.now()
      .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    warehouseRepository.save(warehouseEntity);

    try {
      taskHistoryService.createTaskHistory(new TaskHistoryRequest("Nhập kho thủ công", "Nhập kho sản phẩm thành công, tên: " + warehouseEntity.getProductName(), userId));
      log.info("Nhập kho sản phẩm thành công, tên: " + warehouseEntity.getProductName());
    } catch (Exception e) {
      log.error("Lỗi trong quá trình lưu log");
    }
    Response.ok();
  }

  @Override
  @Transactional
  public Mono<Response<List<WarehouseImportErrorResponse>>> importWarehouseFromExcel(
    FilePart file, String userId) {

    return file
      .content()
      .collectList()
      .flatMap(dataBuffers -> {
        InputStream inputStream = dataBuffers.get(0).asInputStream();
        try {
          Workbook workbook = new XSSFWorkbook(inputStream);
          Sheet sheet = workbook.getSheetAt(0);

          List<WarehouseEntity> warehouseEntityList = new ArrayList<>();
          List<WarehouseImportErrorResponse> errorResponseList = new ArrayList<>();

          int startRow = 1; // Bỏ qua hàng header (row index 0)
          DataFormatter dataFormatter = new DataFormatter();
          LocalDate today = LocalDate.now();

          for (int rowIndex = startRow; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            // --- Đọc dữ liệu từng cột ---
            String productGroup = getCellString(dataFormatter, row, 1);
            String productName = getCellString(dataFormatter, row, 2);
            String origin = getCellString(dataFormatter, row, 3);
            String productDetail = getCellString(dataFormatter, row, 4);
            String mixingSpec = getCellString(dataFormatter, row, 5);
            String rawMfgDate = getCellRawText(row.getCell(6));
            LocalDate mfgDate = parseDate(rawMfgDate);
            String rawExpiryDate = getCellRawText(row.getCell(7));
            LocalDate expiryDate = parseDate(rawExpiryDate);
            String rawImportDate = getCellRawText(row.getCell(8));
            LocalDate importDate = parseDate(rawImportDate);
            String unit = getCellString(dataFormatter, row, 9);
            Integer importQty = parseIntCell(row.getCell(10));
            String importedBy = getCellString(dataFormatter, row, 11);
            String storageLocation = getCellString(dataFormatter, row, 12);
            String note = getCellString(dataFormatter, row, 13);

            // --- Validate ---
            String errorNote = null;

            if (productName == null || productName.isBlank()) {
              errorNote = "Tên sản phẩm không được để trống";
            } else if (importedBy == null || importedBy.isBlank()) {
              errorNote = "Người nhập kho không được để trống";
            } else if (importDate == null) {
              errorNote = "Ngày nhập kho không được để trống";
            } else if (importQty == null || importQty <= 0) {
              errorNote = "Số lượng nhập phải lớn hơn 0";
            }
            else if (expiryDate != null && !expiryDate.isAfter(today)) {
              errorNote = "Ngày hết hạn phải lớn hơn ngày hiện tại";
            }
            else if (rawMfgDate != null && isValidDateFormat(rawMfgDate)) {
              errorNote = "Ngày sản xuất sai định dạng. Chấp nhận: dd/MM/yyyy, dd-MM-yyyy, yyyy-MM-dd";
            }
            else if (rawExpiryDate != null && isValidDateFormat(rawExpiryDate)) {
              errorNote = "Ngày hết hạn sai định dạng. Chấp nhận: dd/MM/yyyy, dd-MM-yyyy, yyyy-MM-dd";
            }
            else if (isValidDateFormat(rawImportDate)) {
              errorNote = "Ngày nhập kho sai định dạng. Chấp nhận: dd/MM/yyyy, dd-MM-yyyy, yyyy-MM-dd";
            }
            else if (existsByProductGroupAndProductNameAndUnit(
              productGroup != null ? productGroup.trim() : null,
              productName.trim(),
              unit != null ? unit.trim() : null,
              mfgDate, expiryDate
            )) {
              errorNote = "Sản phẩm đã tồn tại trong nhóm hàng với cùng đơn vị tính";
            }

            if (errorNote != null) {
              WarehouseImportErrorResponse err = new WarehouseImportErrorResponse();
              err.setProductGroup(productGroup);
              err.setProductName(productName);
              err.setOrigin(origin);
              err.setProductDetail(productDetail);
              err.setMixingSpecification(mixingSpec);
              err.setManufacturingDate(rawMfgDate);
              err.setExpiryDate(rawExpiryDate);
              err.setImportDate(rawImportDate);
              err.setImportedBy(importedBy);
              err.setStorageLocation(storageLocation);
              err.setUnit(unit);
              err.setImportQuantity(importQty);
              err.setNote(note);
              err.setErrorNote(errorNote);
              errorResponseList.add(err);
              continue;
            }

            // --- Map vào entity ---
            WarehouseEntity entity = new WarehouseEntity();
            entity.setProductGroup(productGroup);
            entity.setProductName(productName);
            entity.setOrigin(origin);
            entity.setProductDetail(productDetail);
            entity.setMixingSpecification(mixingSpec);
            entity.setManufacturingDate(mfgDate);
            entity.setExpiryDate(expiryDate);
            entity.setImportDate(importDate);
            entity.setUnit(unit);
            entity.setImportQuantity(importQty);
            entity.setImportedBy(importedBy);
            entity.setStorageLocation(storageLocation);
            entity.setNote(note);
            entity.setCreatedBy(userId);
            entity.setBatchId(LocalDateTime.now()
              .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            warehouseEntityList.add(entity);
          }

          return Mono.defer(() -> {
            TransactionSynchronizationManager.initSynchronization();
            try {
              transactionTemplate.execute(status -> {
                warehouseRepository.saveAll(warehouseEntityList);
                return null;
              });

              if (warehouseEntityList.isEmpty()) {
                return Mono.just(Response.of(errorResponseList).success("Import thất bại", 201));
              }
            } finally {
              TransactionSynchronizationManager.clearSynchronization();
            }

            try {
              taskHistoryService.createTaskHistory(
                new TaskHistoryRequest(
                  "Import nhập kho",
                  "Cập nhật danh sách nhập kho có "
                    + warehouseEntityList.size() + " dòng thành công và "
                    + errorResponseList.size() + " dòng thất bại. Tên file: " + file.filename(), userId
                )
              );
              log.info("Import nhập kho: {} dòng thành công, {} dòng thất bại. File: {}",
                warehouseEntityList.size(), errorResponseList.size(), file.filename());
            } catch (Exception e) {
              log.error("Lỗi lưu log import nhập kho. File: {}", file.filename());
            }

            return Mono.just(Response.of(errorResponseList).success("Import thành công", 201));
          });

        } catch (IOException e) {
          return Mono.just(Response.fail("Import thất bại", 400));
        }
      });
  }

  private boolean existsByProductGroupAndProductNameAndUnit(String productGroup, String productName, String unit, LocalDate mfgDate, LocalDate expiryDate) {
    return warehouseRepository.existsByProductGroupAndProductNameAndUnitAndManufacturingDateAndExpiryDateAndDeletedFalse(productGroup, productName, unit, mfgDate, expiryDate);
  }

// ---- Helper methods ----

  private String getCellString(DataFormatter formatter, Row row, int colIndex) {
    Cell cell = row.getCell(colIndex);
    if (cell == null) return null;
    String val = formatter.formatCellValue(cell).trim();
    return val.isEmpty() ? null : val;
  }

  private String getCellRawText(Cell cell) {
    if (cell == null) return null;
    if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
      // Excel date serial → format về dd/MM/yyyy
      return cell.getLocalDateTimeCellValue()
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    String raw = new DataFormatter().formatCellValue(cell).trim();
    return raw.isEmpty() ? null : raw;
  }

  private LocalDate parseDate(String raw) {
    if (raw == null || raw.isBlank()) return null;
    for (String pattern : List.of("dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd")) {
      try {
        return LocalDate.parse(raw, DateTimeFormatter.ofPattern(pattern));
      } catch (DateTimeParseException ignored) {
      }
    }
    return null;
  }

  private boolean isValidDateFormat(String raw) {
    for (String pattern : List.of("dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd")) {
      try {
        LocalDate.parse(raw, DateTimeFormatter.ofPattern(pattern));
        return false;
      } catch (DateTimeParseException ignored) {
      }
    }
    return true;
  }

  private Integer parseIntCell(Cell cell) {
    if (cell == null) return null;
    try {
      if (cell.getCellType() == CellType.NUMERIC) {
        return (int) cell.getNumericCellValue();
      }
      String raw = new DataFormatter().formatCellValue(cell).trim();
      return raw.isEmpty() ? null : Integer.parseInt(raw);
    } catch (Exception e) {
      return null;
    }
  }

}

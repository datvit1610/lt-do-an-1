package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.application.command.request.task_history.TaskHistoryRequest;
import com.codec.system.application.command.request.warehouse.*;
import com.codec.system.application.command.response.export_excel.ExportExcelHistoryResponse;
import com.codec.system.application.command.response.export_excel.ExportWarehouseResponse;
import com.codec.system.application.command.response.warehouse.ExportHistoryResponse;
import com.codec.system.application.command.response.warehouse.ListWarehouseResponse;
import com.codec.system.application.service.TaskHistoryService;
import com.codec.system.application.service.WarehouseInventoryService;
import com.codec.system.common.utils.Utils;
import com.codec.system.domain.entity.ExportHistoryEntity;
import com.codec.system.domain.entity.NotificationConfigEntity;
import com.codec.system.domain.entity.WarehouseEntity;
import com.codec.system.domain.repository.ExportHistoryRepository;
import com.codec.system.domain.repository.NotificationConfigRepository;
import com.codec.system.domain.repository.WarehouseRepository;
import com.codec.system.pagination.domain.CodecSystemApplicationPage;
import com.codec.system.pagination.domain.CodecSystemApplicationPageable;
import com.codec.system.pagination.infrastructure.primary.RestCodecSystemApplicationPage;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class WarehouseInventoryServiceImpl implements WarehouseInventoryService {
  private final WarehouseRepository warehouseRepository;
  private final TaskHistoryService taskHistoryService;
  private final ExportHistoryRepository exportHistoryRepository;
  private final NotificationConfigRepository notificationConfigRepository;

  // Service
  @Override
  public Response<RestCodecSystemApplicationPage<ListWarehouseResponse>> searchWarehouse(
    WarehouseSearchRequest request, Pageable pageable) {

    // Lấy cấu hình ngưỡng
    NotificationConfigEntity config = notificationConfigRepository.findFirstByOrderByIdAsc();
    int days = (config != null && config.getExpiredDayNotify() != null)
      ? config.getExpiredDayNotify() : 30;

    LocalDate today         = LocalDate.now();
    LocalDate thresholdDate = today.plusDays(days);

    Page<Tuple> result = warehouseRepository.searchWarehouse(
      request.getProductGroup(),
      request.getProductName(),
      request.getOrigin(),
      request.getProductDetail(),
      request.getStorageLocation(),
      request.getImportDateFrom(),
      request.getImportDateTo(),
      request.getExpiryDateFrom(),
      request.getExpiryDateTo(),
      request.getStatus(),  // thêm vào
      request.getStockStatus(), // thêm vào
      request.getQrStatus(), // thêm vào
      today,
      thresholdDate,
      pageable
    );

    List<ListWarehouseResponse> responseList = result.getContent().stream()
      .map(tuple -> {
        ListWarehouseResponse response = new ListWarehouseResponse(tuple);
          response.setDaysUntilExpiry(
            Utils.calculateDaysUntilExpiry(response.getExpiryDate())
          );
          if (response.getStockStatus() == 2) {
            response.setStatus(null);
            response.setDaysUntilExpiry(null);
          }
        return response;
      })
      .toList();

    long currentCount = result.getTotalElements();
    CodecSystemApplicationPageable codecPageable = new CodecSystemApplicationPageable(
      pageable.getPageNumber(), pageable.getPageSize());

    return Response.of(RestCodecSystemApplicationPage
      .from(CodecSystemApplicationPage
        .of(responseList, codecPageable, currentCount), qrcode -> qrcode));
  }

  @Override
  public Response<RestCodecSystemApplicationPage<ListWarehouseResponse>> searchForExportWarehouse(
    WarehouseSearchRequest request, Pageable pageable) {

    // Lấy cấu hình ngưỡng
    NotificationConfigEntity config = notificationConfigRepository.findFirstByOrderByIdAsc();
    int days = (config != null && config.getExpiredDayNotify() != null)
      ? config.getExpiredDayNotify() : 30;

    LocalDate today         = LocalDate.now();
    LocalDate thresholdDate = today.plusDays(days);

    Page<Tuple> result = warehouseRepository.searchForExportWarehouse(
      request.getProductGroup(),
      request.getProductName(),
      request.getOrigin(),
      request.getStorageLocation(),
      today,
      thresholdDate,
      pageable
    );

    List<ListWarehouseResponse> responseList = result.getContent().stream()
      .map(tuple -> {
        ListWarehouseResponse response = new ListWarehouseResponse(tuple);
        if (response.getStockStatus() == 2) {
          response.setStatus(null);
        } else {
          response.setDaysUntilExpiry(
            Utils.calculateDaysUntilExpiry(response.getExpiryDate())
          );
        }

//        // Set status từ query
//        Integer status = tuple.get(1, Integer.class);
//        response.setStatus(status);

        return response;
      })
      .toList();

    long currentCount = result.getTotalElements();
    CodecSystemApplicationPageable codecPageable = new CodecSystemApplicationPageable(
      pageable.getPageNumber(), pageable.getPageSize());

    return Response.of(RestCodecSystemApplicationPage
      .from(CodecSystemApplicationPage
        .of(responseList, codecPageable, currentCount), qrcode -> qrcode));
  }

  @Override
  public byte[] exportWarehouse(WarehouseSearchRequest request) {

    // Lấy cấu hình ngưỡng
    NotificationConfigEntity config = notificationConfigRepository.findFirstByOrderByIdAsc();
    int days = (config != null && config.getExpiredDayNotify() != null)
      ? config.getExpiredDayNotify() : 30;

    LocalDate today         = LocalDate.now();
    LocalDate thresholdDate = today.plusDays(days);

    List<Tuple> result = warehouseRepository.exportWarehouse(
      request.getProductGroup(),
      request.getProductName(),
      request.getOrigin(),
      request.getProductDetail(),
      request.getStorageLocation(),
      request.getImportDateFrom(),
      request.getImportDateTo(),
      request.getExpiryDateFrom(),
      request.getExpiryDateTo(),
      request.getStatus(),  // thêm vào
      request.getStockStatus(), // thêm vào
      request.getQrStatus(), // thêm vào
      today,
      thresholdDate
    );

    List<ListWarehouseResponse> responseList = result.stream()
      .map(tuple -> {
        ListWarehouseResponse response = new ListWarehouseResponse(tuple);
        response.setDaysUntilExpiry(
          Utils.calculateDaysUntilExpiry(response.getExpiryDate())
        );

        return response;
      })
      .toList();
    AtomicInteger stt = new AtomicInteger(1);
    List<ExportWarehouseResponse> exportData = new ArrayList<>();
    for (ListWarehouseResponse item : responseList) {
      ExportWarehouseResponse exportItem = new ExportWarehouseResponse();
      exportItem.setStt(stt.getAndIncrement());
      exportItem.setProductGroup(item.getProductGroup());
      exportItem.setProductName(item.getProductName());
      exportItem.setOrigin(item.getOrigin());
      exportItem.setProductDetail(item.getProductDetail());
      exportItem.setMixingSpecification(item.getMixingSpecification());
      exportItem.setManufacturingDate(item.getManufacturingDate());
      exportItem.setExpiryDate(item.getExpiryDate());
      exportItem.setImportDate(item.getImportDate());
      exportItem.setUnit(item.getUnit());
      exportItem.setImportQuantity(item.getImportQuantity());
      exportItem.setImportedBy(item.getImportedBy());
      exportItem.setStorageLocation(item.getStorageLocation());
      exportItem.setNote(item.getNote());
      exportItem.setDaysUntilExpiry(item.getDaysUntilExpiry());
      exportItem.setStatus(item.getStatus() == 1 ? "Còn hạn" : (item.getStatus() == 2 ? "Sắp hết hạn" : (item.getStatus() == 3 ? "Đã hết hạn" : "Vô thời hạn")));
      exportItem.setStockStatus(item.getStockStatus() == 1 ? "Còn hàng" : "Hết hàng");
      exportItem.setExportQuantity(item.getExportQuantity());
      exportItem.setRemainingQuantity(item.getRemainingQuantity());
      exportData.add(exportItem);
    }

    try (
      InputStream templateStream = this.getClass().getClassLoader().getResourceAsStream(
        "template/quan_ly_tong_kho_template.xlsx"
      );
      ByteArrayOutputStream out = new ByteArrayOutputStream()
    ) {
      if (templateStream == null) {
        throw new RuntimeException("Không tìm thấy file template");
      }

      Context context = new Context();
      context.putVar("warehouses", exportData);

      JxlsHelper.getInstance().processTemplate(templateStream, out, context);

      return out.toByteArray();

    } catch (Exception e) {
      throw new RuntimeException("Lỗi export file Excel", e);
    }


  }

  @Override
  @Transactional
  public void                                                                                                                                                                                                                                            updateWarehouse(String id,
                              CreateWarehouseRequest request,
                              String userId) {

    // Tìm sản phẩm
    WarehouseEntity warehouseEntity = warehouseRepository.findById(id)
      .orElseThrow(() ->
        new RuntimeException("Không tìm thấy sản phẩm trong kho"));

    // Check ngày hết hạn
    if (request.getExpiryDate() != null
      && request.getExpiryDate().isBefore(LocalDate.now())) {

      throw new RuntimeException(
        "Ngày hết hạn phải lớn hơn ngày hiện tại");
    }

    // Check ngày sản xuất
    if (request.getManufacturingDate() != null
      && request.getExpiryDate() != null
      && request.getManufacturingDate()
      .isAfter(request.getExpiryDate())) {

      throw new RuntimeException(
        "Ngày sản xuất phải nhỏ hơn ngày hết hạn");
    }

    // Check trùng sản phẩm
    boolean exists = warehouseRepository
      .existsWarehouseForUpdate(
        request.getProductGroup().trim(),
        request.getProductName().trim(),
        request.getUnit().trim(),
        id
      );

    if (exists) {
      throw new RuntimeException(
        "Sản phẩm đã tồn tại trong nhóm hàng với cùng đơn vị tính");
    }
    if (request.getImportQuantity() != null
      && request.getImportQuantity() < 0) {

      throw new RuntimeException(
        "Số lượng nhập phải lớn hơn hoặc bằng 0");
    }

    // Update dữ liệu
    warehouseEntity.setProductGroup(request.getProductGroup());
    warehouseEntity.setProductName(request.getProductName());
    warehouseEntity.setOrigin(request.getOrigin());
    warehouseEntity.setProductDetail(request.getProductDetail());
    warehouseEntity.setMixingSpecification(request.getMixingSpecification());
    warehouseEntity.setManufacturingDate(request.getManufacturingDate());
    warehouseEntity.setExpiryDate(request.getExpiryDate());
    warehouseEntity.setImportDate(request.getImportDate());
    warehouseEntity.setUnit(request.getUnit());
    warehouseEntity.setImportQuantity(request.getImportQuantity());
    warehouseEntity.setImportedBy(request.getImportedBy());
    warehouseEntity.setStorageLocation(request.getStorageLocation());
    warehouseEntity.setNote(request.getNote());

    // audit
    warehouseEntity.setModifiedBy(userId);
    warehouseEntity.setModifiedDate(new Date());

    warehouseRepository.save(warehouseEntity);

    try {
      taskHistoryService.createTaskHistory(
        new TaskHistoryRequest(
          "Cập nhật kho hàng",
          "Cập nhật sản phẩm thành công, tên: "
            + warehouseEntity.getProductName(),
          userId
        )
      );

      log.info("Cập nhật sản phẩm thành công, tên: {}",
        warehouseEntity.getProductName());

    } catch (Exception e) {
      log.error("Lỗi trong quá trình lưu log");
    }

    Response.ok();
  }

  @Override
  @Transactional
  public void deleteWarehouse(String id, String userId) {

    // Check tồn tại
    WarehouseEntity warehouseEntity =
      warehouseRepository.findByIdAndDeletedFalse(id)
        .orElseThrow(() ->
          new RuntimeException(
            "Không tìm thấy sản phẩm trong kho"));

    //check đã có lịch sử xuất kho chưa
    boolean hasExportHistory = exportHistoryRepository.existsByWarehouseId(warehouseEntity.getId());
    if (hasExportHistory) {
      throw new RuntimeException(
        "Không thể xóa sản phẩm đã có lịch sử xuất kho");
    }

    // Xóa mềm
    warehouseEntity.setDeleted(true);

    // audit
    warehouseEntity.setModifiedBy(userId);
    warehouseEntity.setModifiedDate(new Date());

    warehouseRepository.save(warehouseEntity);

    try {

      taskHistoryService.createTaskHistory(
        new TaskHistoryRequest(
          "Xóa kho hàng",
          "Xóa sản phẩm thành công, tên: "
            + warehouseEntity.getProductName(),
          userId
        )
      );

      log.info("Xóa sản phẩm thành công, tên: {}",
        warehouseEntity.getProductName());

    } catch (Exception e) {

      log.error("Lỗi trong quá trình lưu log");
    }

    Response.ok();
  }


  @Override
  @Transactional
  public void exportWarehouse(List<ExportProductRequest> request, String userId) {
    if (request == null
      || request.isEmpty()) {
      throw new RuntimeException(
        "Danh sách xuất kho không được trống");
    }

    List<ExportHistoryEntity> exportHistories = new ArrayList<>();
    List<String> productSummaryList = new ArrayList<>(); // thêm dòng này
    for (ExportProductRequest item : request) {

      WarehouseEntity warehouseEntity = warehouseRepository.findByIdAndDeletedFalse(item.getWarehouseId())
        .orElseThrow(() ->
          new RuntimeException(
            "Không tìm thấy sản phẩm"));

      Integer currentQuantity = warehouseEntity.getImportQuantity();

      if (currentQuantity == null) {
        currentQuantity = 0;
      }

      Integer requestExportQuantity = item.getExportQuantity();

      if (requestExportQuantity == null || requestExportQuantity <= 0) {
        continue;
      }

      // nếu xuất lớn hơn tồn
      // thì xuất hết
      Integer actualExportQuantity = Math.min(currentQuantity, requestExportQuantity);
//      Integer remainQuantity = currentQuantity - actualExportQuantity;

      // update tồn kho
//      warehouseEntity.setImportQuantity(remainQuantity);
      warehouseEntity.setModifiedBy(userId);
      warehouseEntity.setModifiedDate(new Date());
      warehouseEntity.setExportQuantity(
        (warehouseEntity.getExportQuantity() == null ? 0 : warehouseEntity.getExportQuantity())
          + actualExportQuantity
      );
      warehouseRepository.save(warehouseEntity);

      // lưu lịch sử xuất kho
      ExportHistoryEntity history = new ExportHistoryEntity();
      history.setWarehouseId(warehouseEntity.getId());
      history.setExportQuantity(actualExportQuantity);
      history.setExportedBy(userId);
      history.setExportDate(new Date());
      exportHistories.add(history);

      log.info("Xuất kho sản phẩm: {}, SL xuất: {}", warehouseEntity.getProductName(), actualExportQuantity);
      // Lưu tên ngay tại đây, đã có warehouseEntity rồi không cần query lại
      productSummaryList.add(warehouseEntity.getProductName() + " (SL: " + actualExportQuantity + ")");
    }
    exportHistoryRepository.saveAll(exportHistories);
    String productSummary = String.join(", ", productSummaryList);
    try {
      taskHistoryService.createTaskHistory(new TaskHistoryRequest(
        "Xuất kho", "Xuất kho sản phẩm: " + productSummary, userId)
      );
    } catch (Exception e) {
      log.error("Lỗi lưu lịch sử tác vụ");
    }

    Response.ok();
  }


  /*
  Hàm tìm kiếm lịch sử xuất kho với các tiêu chí lọc và phân trang
   */
  @Override
  public Response<RestCodecSystemApplicationPage<ExportHistoryResponse>> searchExportHistory(ExportHistorySearchRequest request, Pageable pageable) {

    Page<Tuple> result =
      exportHistoryRepository.searchExportHistory(
        request.getExportDateFrom(),
        request.getExportDateTo(),
        request.getProductGroup(),
        request.getProductName(),
        request.getExportedBy(),
        request.getStorageLocation(),
        pageable
      );

    List<ExportHistoryResponse> responseList =
      result.getContent()
        .stream()
        .map(ExportHistoryResponse::new)
        .toList();

    long currentCount = result.getTotalElements();
    CodecSystemApplicationPageable codecPageable = new CodecSystemApplicationPageable(pageable.getPageNumber(), pageable.getPageSize());
    return Response.of(RestCodecSystemApplicationPage
      .from(CodecSystemApplicationPage
        .of(responseList, codecPageable, currentCount), qrcode -> qrcode));
  }


  @Override
  public byte[] exportHistory(ExportHistorySearchRequest request) {

    List<Tuple> result =
      exportHistoryRepository.exportHistory(
        request.getExportDateFrom(),
        request.getExportDateTo(),
        request.getProductGroup(),
        request.getProductName(),
        request.getExportedBy(),
        request.getStorageLocation()
      );

    List<ExportHistoryResponse> responseList = result
        .stream()
        .map(ExportHistoryResponse::new)
        .toList();

    List<ExportExcelHistoryResponse> exportData = new ArrayList<>();
    AtomicInteger stt = new AtomicInteger(1);
    for (ExportHistoryResponse item : responseList) {
      ExportExcelHistoryResponse exportItem = new ExportExcelHistoryResponse();
      exportItem.setStt(stt.getAndIncrement());
      exportItem.setExportDate(item.getExportDate());
      exportItem.setProductGroup(item.getProductGroup());
      exportItem.setProductName(item.getProductName());
      exportItem.setOrigin(item.getOrigin());
      exportItem.setProductDetail(item.getProductDetail());
      exportItem.setMixingSpecification(item.getMixingSpecification());
      exportItem.setExpiryDate(item.getExpiryDate());
      exportItem.setImportDate(item.getImportDate());
      exportItem.setUnit(item.getUnit());
      exportItem.setExportQuantity(item.getExportQuantity());
      exportItem.setExportedBy(item.getExportedBy());
      exportItem.setStorageLocation(item.getStorageLocation());
      exportItem.setOrigin(item.getOrigin());
      exportData.add(exportItem);
    }

    try (
      InputStream templateStream = this.getClass().getClassLoader().getResourceAsStream(
        "template/lich_su_xuat_kho_template.xlsx"
      );
      ByteArrayOutputStream out = new ByteArrayOutputStream()
    ) {
      if (templateStream == null) {
        throw new RuntimeException("Không tìm thấy file template");
      }

      Context context = new Context();
      context.putVar("historys", exportData);

      JxlsHelper.getInstance().processTemplate(templateStream, out, context);

      return out.toByteArray();

    } catch (Exception e) {
      throw new RuntimeException("Lỗi export file Excel", e);
    }
  }


  @Override
  @Transactional
  public Response<Void> bulkUpdate(BulkUpdateWarehouseRequest request, String userId) {
    List<WarehouseEntity> entities = warehouseRepository.findAllById(request.getIds());

    if (entities.isEmpty()) {
      return Response.fail("Không tìm thấy sản phẩm", 400);
    }

    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    // Lưu thông tin trước khi update
    List<String> summaryList = new ArrayList<>();
    for (WarehouseEntity entity : entities) {
      // Lấy giá trị cũ trước khi update
      String oldValue = getFieldValue(entity, request.getFieldName());
      switch (request.getFieldName()) {
        case "productGroup"        -> entity.setProductGroup(request.getValue());
        case "productName"         -> entity.setProductName(request.getValue());
        case "origin"              -> entity.setOrigin(request.getValue());
        case "productDetail"       -> entity.setProductDetail(request.getValue());
        case "mixingSpecification" -> entity.setMixingSpecification(request.getValue());
        case "unit"                -> entity.setUnit(request.getValue());
        case "importedBy"          -> entity.setImportedBy(request.getValue());
        case "storageLocation"     -> entity.setStorageLocation(request.getValue());
        case "note"                -> entity.setNote(request.getValue());
        case "importQuantity"      -> {
          if (request.getValue() == null) {
            throw new RuntimeException("Số lượng nhập không được trống");
          }
          entity.setImportQuantity(Integer.parseInt(request.getValue()));
        }
        case "manufacturingDate"   -> entity.setManufacturingDate(
          request.getValue() != null ? LocalDate.parse(request.getValue(), fmt) : null);
        case "expiryDate"          -> entity.setExpiryDate(
          request.getValue() != null ? LocalDate.parse(request.getValue(), fmt) : null);
        case "importDate"          -> entity.setImportDate(
          request.getValue() != null ? LocalDate.parse(request.getValue(), fmt) : null);
        default -> throw new RuntimeException("Cột '" + request.getFieldName() + "' không hợp lệ");
      }
      entity.setModifiedBy(userId);
      entity.setModifiedDate(new Date());
      // Ghi lại: tên sản phẩm | giá trị cũ -> giá trị mới
      summaryList.add(entity.getProductName()
        + " [" + oldValue + " -> " + (request.getValue() != null ? request.getValue() : "null") + "]");
    }

    warehouseRepository.saveAll(entities);
    String summary = String.join(", ", summaryList);

    try {
      taskHistoryService.createTaskHistory(new TaskHistoryRequest(
        "Cập nhật hàng loạt",
        "Cập nhật cột '" + request.getFieldName() + "': " + summary,
        userId));
    } catch (Exception e) {
      log.error("Lỗi lưu lịch sử tác vụ bulk update");
    }

    return Response.<Void>of(null).success("Cập nhật thành công", 201);
  }

  private String getFieldValue(WarehouseEntity entity, String fieldName) {
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    return switch (fieldName) {
      case "productGroup"        -> entity.getProductGroup();
      case "productName"         -> entity.getProductName();
      case "origin"              -> entity.getOrigin();
      case "productDetail"       -> entity.getProductDetail();
      case "mixingSpecification" -> entity.getMixingSpecification();
      case "unit"                -> entity.getUnit();
      case "importedBy"          -> entity.getImportedBy();
      case "storageLocation"     -> entity.getStorageLocation();
      case "note"                -> entity.getNote();
      case "importQuantity"      -> entity.getImportQuantity() != null
        ? entity.getImportQuantity().toString() : null;
      case "manufacturingDate"   -> entity.getManufacturingDate() != null
        ? entity.getManufacturingDate().format(fmt) : null;
      case "expiryDate"          -> entity.getExpiryDate() != null
        ? entity.getExpiryDate().format(fmt) : null;
      case "importDate"          -> entity.getImportDate() != null
        ? entity.getImportDate().format(fmt) : null;
      default -> null;
    };
  }

}

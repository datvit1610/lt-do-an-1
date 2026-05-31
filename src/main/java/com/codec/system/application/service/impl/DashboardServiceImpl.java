package com.codec.system.application.service.impl;

import codec.common.Response;
import com.codec.system.application.command.response.dashboard.DashboardGroupResponse;
import com.codec.system.application.command.response.dashboard.DashboardNearExpiryResponse;
import com.codec.system.application.command.response.dashboard.DashboardSummaryResponse;
import com.codec.system.application.service.DashboardService;
import com.codec.system.common.utils.Utils;
import com.codec.system.domain.entity.NotificationConfigEntity;
import com.codec.system.domain.repository.NotificationConfigRepository;
import com.codec.system.domain.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
  private final WarehouseRepository warehouseRepository;
  private final NotificationConfigRepository notificationConfigRepository;

  private LocalDate getThresholdDate() {
    NotificationConfigEntity config = notificationConfigRepository.findFirstByOrderByIdAsc();
    int days = (config != null && config.getExpiredDayNotify() != null)
      ? config.getExpiredDayNotify() : 30;
    return LocalDate.now().plusDays(days);
  }

  /*
Lấy dữ liệu tổng quan
   */
  @Override
  public Response<DashboardSummaryResponse> getSummary() {
    LocalDate today = LocalDate.now();
    LocalDate thresholdDate = getThresholdDate();

    DashboardSummaryResponse response = new DashboardSummaryResponse();
    response.setTotalProducts(warehouseRepository.countByDeletedFalse());
    response.setExpiredCount(warehouseRepository.countExpired(today));
    response.setNearExpiryCount(warehouseRepository.findNearExpiry(today, thresholdDate));
    response.setImportedTodayCount(warehouseRepository.countByImportDateAndDeletedFalse(LocalDate.now()));

    return Response.of(response).success("Lấy tổng quan thành công", 200);
  }

  /*
  Lấy dữ liệu biểu đồ theo nhóm
   */
  @Override
  public Response<List<DashboardGroupResponse>> getByGroup() {
    LocalDate today         = LocalDate.now();
    LocalDate thresholdDate = getThresholdDate();

    Map<String, Long> totalMap = warehouseRepository.countByGroup()
      .stream()
      .collect(Collectors.toMap(
        row -> (String) row[0],
        row -> ((Number) row[1]).longValue()
      ));

    Map<String, Long> nearExpiryMap = warehouseRepository.countNearExpiryByGroup(today, thresholdDate)
      .stream()
      .collect(Collectors.toMap(
        row -> (String) row[0],
        row -> ((Number) row[1]).longValue()
      ));

    List<DashboardGroupResponse> result = totalMap.entrySet().stream()
      .map(entry -> {
        String group       = entry.getKey();
        long totalRemaining    = entry.getValue();           // tổng SL còn lại
        long nearExpiryRemaining = nearExpiryMap.getOrDefault(group, 0L); // SL còn lại sắp hết hạn
        long validRemaining    = totalRemaining - nearExpiryRemaining;     // SL còn lại còn hạn

        double percent = totalRemaining > 0
          ? Math.round((nearExpiryRemaining * 100.0 / totalRemaining) * 10) / 10.0
          : 0.0;

        DashboardGroupResponse res = new DashboardGroupResponse();
        res.setProductGroup(group);
        res.setTotal(totalRemaining);
        res.setNearExpiryCount(nearExpiryRemaining);
        res.setValidCount(validRemaining);
        res.setNearExpiryPercent(percent);
        return res;
      })
      .filter(res -> res.getNearExpiryCount() > 0)
      .collect(Collectors.toList());

    return Response.of(result).success("Lấy thống kê theo nhóm thành công", 200);
  }

  /*
  Lấy danh sách sản phẩm gần hết hạn
   */
  @Override
  public Response<List<DashboardNearExpiryResponse>> getNearExpiry() {
    LocalDate thresholdDate = getThresholdDate();

    List<DashboardNearExpiryResponse> result = warehouseRepository
      .findNearExpiryAndExpiryDate(thresholdDate)
      .stream()
      .map(w -> {
        DashboardNearExpiryResponse res = new DashboardNearExpiryResponse();
        res.setProductName(w.getProductName());
        res.setProductGroup(w.getProductGroup());
        res.setStorageLocation(w.getStorageLocation());
        res.setExpiryDate(w.getExpiryDate());
        res.setDaysUntilExpiry(Utils.calculateDaysUntilExpiry(w.getExpiryDate()));
        return res;
      })
      .collect(Collectors.toList());

    return Response.of(result).success("Lấy danh sách sắp hết hạn thành công", 200);
  }
}

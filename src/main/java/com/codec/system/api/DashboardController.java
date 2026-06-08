package com.codec.system.api;

import codec.common.Response;
import com.codec.system.application.command.response.dashboard.DashboardOverviewResponse;
import com.codec.system.application.command.response.dashboard.LoanStatusResponse;
import com.codec.system.application.command.response.dashboard.Top5DeviceResponse;
import com.codec.system.application.service.DashboardService;
import com.codec.system.application.service.authen.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;
  private final JwtUtil jwtUtil;

  /**
   * GET /api/dashboard/overview
   *
   * Tổng quan hệ thống — 6 card trên màn hình Dashboard.
   *
   * @param fromDate (optional) yyyy-MM-dd — mặc định 30 ngày trước
   * @param toDate   (optional) yyyy-MM-dd — mặc định hôm nay

   */
  @Operation(summary = "Tổng quan hệ thống: đầu thiết bị, tổng số lượng, lượt mượn, lượt mất, giảng viên, sinh viên")
  @GetMapping("/dashboard/overview")
  public Mono<Response<DashboardOverviewResponse>> getOverview(
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "das-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    DashboardOverviewResponse data = dashboardService.getOverview(fromDate, toDate);
    return Mono.just(Response.of(data).success("Thành công", 200));
  }

  @Operation(summary = "Top 5 thiết bị được mượn nhiều nhất trong khoảng thời gian ( đường ngang )")
  @GetMapping("/top5-devices")
  public Mono<Response<List<Top5DeviceResponse>>> getTop5BorrowedDevices(
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "das-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    List<Top5DeviceResponse> data = dashboardService.getTop5BorrowedDevices(fromDate, toDate);
    return Mono.just(Response.of(data).success("Thành công", 200));
  }

  @Operation(summary = "Thống kê phiếu mượn theo trạng thái: đang mượn, đã trả, trả chậm, mất thiết bị ( tròn )")
  @GetMapping("/loan-status-stats")
  public Mono<Response<LoanStatusResponse>> getLoanStatusStats(
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "das-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    LoanStatusResponse data = dashboardService.getLoanStatusStats(fromDate, toDate);
    return Mono.just(Response.of(data).success("Thành công", 200));
  }
}

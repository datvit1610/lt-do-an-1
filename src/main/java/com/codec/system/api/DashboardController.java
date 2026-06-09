package com.codec.system.api;

import codec.common.Response;
import com.codec.system.application.command.response.dashboard.*;
import com.codec.system.application.service.DashboardService;
import com.codec.system.application.service.authen.JwtUtil;
import com.codec.system.common.utils.TrendMode;
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
  @GetMapping("/dashboard/top5-devices")
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
  @GetMapping("/dashboard/loan-status-stats")
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

  @Operation(summary = "Xu hướng lượt mượn theo thời gian — dùng cho biểu đồ line")
  @GetMapping("/dashboard/loan-trend")
  public Mono<Response<LoanTrendResponse>> getLoanTrend(
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,
    @RequestParam(required = false) TrendMode groupBy,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "das-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    LoanTrendResponse data = dashboardService.getLoanTrend(fromDate, toDate, groupBy);
    return Mono.just(Response.of(data).success("Thành công", 200));
  }

  @Operation(summary = "Thống kê lượt mượn theo loại thiết bị — dùng cho biểu đồ donut")
  @GetMapping("/dashboard/device-type-stats")
  public Mono<Response<DeviceTypeLoanResponse>> getLoansByDeviceType(
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "das-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    DeviceTypeLoanResponse data = dashboardService.getLoansByDeviceType(fromDate, toDate);
    return Mono.just(Response.of(data).success("Thành công", 200));
  }

  @Operation(summary = "Top người mượn nhiều nhất — filter theo vai trò: Sinh viên / Giảng viên / tất cả")
  @GetMapping("/dashboard/top-borrowers")
  public Mono<Response<List<TopBorrowerResponse>>> getTopBorrowers(
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,
    @RequestParam(required = false) String roleName,
    @RequestParam(defaultValue = "10") int topN,
    @RequestHeader("Authorization") String authHeader
  ) {
    String userId = jwtUtil.checkPermission(authHeader, "das-v");
    if (userId.equals("Api không có quyền truy cập") || userId.equals("Token không hợp lệ")) {
      return Mono.just(Response.fail(userId, 403));
    }
    List<TopBorrowerResponse> data = dashboardService.getTopBorrowers(fromDate, toDate, roleName, topN);
    return Mono.just(Response.of(data).success("Thành công", 200));
  }
}

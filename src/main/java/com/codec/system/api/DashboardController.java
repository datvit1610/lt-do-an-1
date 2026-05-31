package com.codec.system.api;

import codec.common.Response;
import com.codec.system.application.command.response.dashboard.DashboardGroupResponse;
import com.codec.system.application.command.response.dashboard.DashboardNearExpiryResponse;
import com.codec.system.application.command.response.dashboard.DashboardSummaryResponse;
import com.codec.system.application.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class DashboardController {
  private final DashboardService dashboardService;

  @Comment("API lấy dữ liệu tổng quan cho dashboard")
  @GetMapping("/dashboard/summary")
  public Mono<Response<DashboardSummaryResponse>> getSummary() {
    return Mono.just(Response.of(dashboardService.getSummary().getData()).success("Thành công", 200));
  }

  @Comment("API lấy dữ liệu biểu đồ theo nhóm cho dashboard")
  @GetMapping("/dashboard/by-group")
  public Mono<Response<List<DashboardGroupResponse>>> getByGroup() {
    return Mono.just(Response.of(dashboardService.getByGroup().getData()).success("Thành công", 200));
  }

  @Comment("API lấy danh sách sản phẩm gần hết hạn cho dashboard")
  @GetMapping("/dashboard/near-expiry")
  public Mono<Response<List<DashboardNearExpiryResponse>>> getNearExpiry() {
    return Mono.just(Response.of(dashboardService.getNearExpiry().getData()).success("Thành công", 200));
  }
}

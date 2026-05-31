package com.codec.system.application.service;

import codec.common.Response;
import com.codec.system.application.command.response.dashboard.DashboardGroupResponse;
import com.codec.system.application.command.response.dashboard.DashboardNearExpiryResponse;
import com.codec.system.application.command.response.dashboard.DashboardSummaryResponse;

import java.util.List;

public interface DashboardService {
  // Lấy dữ liệu tổng quan
  Response<DashboardSummaryResponse> getSummary();

  // Lấy dữ liệu biểu đồ theo nhóm
  Response<List<DashboardGroupResponse>> getByGroup();

  // Lấy danh sách sản phẩm gần hết hạn
  Response<List<DashboardNearExpiryResponse>> getNearExpiry();
}

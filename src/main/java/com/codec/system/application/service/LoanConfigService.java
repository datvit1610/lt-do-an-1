package com.codec.system.application.service;

import codec.common.Response;
import com.codec.system.application.command.request.loan.LoanConfigRequest;
import com.codec.system.application.command.response.loan.LoanConfigResponse;

public interface LoanConfigService {
  /** Ngưỡng phút chậm trả mặc định khi chưa có cấu hình. */
  int DEFAULT_LATE_THRESHOLD_MINUTES = 15;

  Response<LoanConfigResponse> getConfig();

  void saveConfig(LoanConfigRequest request, String userId);

  /** Lấy nhanh ngưỡng phút chậm trả hiện hành (dùng khi tính trạng thái trễ). */
  int getLateThresholdMinutes();
}

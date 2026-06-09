package com.codec.system.application.service;

import com.codec.system.application.command.response.dashboard.*;
import com.codec.system.common.utils.TrendMode;

import java.util.Date;
import java.util.List;

public interface DashboardService {
  DashboardOverviewResponse getOverview(Date fromDate, Date toDate);
  List<Top5DeviceResponse> getTop5BorrowedDevices(Date fromDate, Date toDate);
  LoanStatusResponse getLoanStatusStats(Date fromDate, Date toDate);
  LoanTrendResponse getLoanTrend(Date fromDate, Date toDate, TrendMode mode);
  DeviceTypeLoanResponse getLoansByDeviceType(Date fromDate, Date toDate);
  List<TopBorrowerResponse> getTopBorrowers(Date fromDate, Date toDate,
                                            String roleName, int topN);
}

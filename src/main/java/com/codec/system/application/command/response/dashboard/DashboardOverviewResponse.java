package com.codec.system.application.command.response.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * Response DTO cho API tổng quan hệ thống dashboard.
 * Tương ứng với 6 card trên màn hình Dashboard.
 */
@Data
@Builder
public class DashboardOverviewResponse {

  /**
   * Đầu thiết bị — số dòng trong bảng devices có status=1 và deleted=false
   */
  private Long totalDeviceTypes;

  /**
   * Tổng số lượng — SUM(quantity) của devices có status=1 và deleted=false
   */
  private Long totalDeviceQuantity;

  /**
   * Lượt mượn — COUNT loans có status IN (1,2,3,4) và borrowDate trong khoảng [from, to]
   */
  private Long totalLoans;

  /**
   * Lượt mất — COUNT loans có status=4 và borrowDate trong khoảng [from, to]
   */
  private Long totalLost;

  /**
   * Số tài khoản Giảng viên — join role.name = 'Giảng viên', deleted=false
   */
  private Long totalTeachers;

  /**
   * Số tài khoản Sinh viên — join role.name = 'Sinh viên', deleted=false
   */
  private Long totalStudents;

  /**
   * Khoảng thời gian filter (trả về để FE hiển thị)
   */
  private Date fromDate;
  private Date toDate;
}

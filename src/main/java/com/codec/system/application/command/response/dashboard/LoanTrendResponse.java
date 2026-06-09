package com.codec.system.application.command.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LoanTrendResponse {

  /** Danh sách điểm dữ liệu theo thời gian */
  private List<TrendPoint> data;

  /** Tổng lượt mượn trong kỳ */
  private Long total;

  /** Cao điểm (giá trị lớn nhất) */
  private Long peak;

  /** Nhãn của kỳ cao điểm */
  private String peakLabel;

  /** Trung bình mỗi kỳ (làm tròn) */
  private Long average;

  @Data
  @AllArgsConstructor
  public static class TrendPoint {
    /** Nhãn hiển thị trên trục X: "01/06", "T2/06", "Th6/26" */
    private String label;

    /** Số lượt mượn trong kỳ */
    private Long loans;
  }
}

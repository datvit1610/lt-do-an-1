package com.codec.system.application.command.response.dashboard;

import jakarta.persistence.Tuple;
import lombok.Data;

/**
 * Thống kê phiếu mượn theo từng trạng thái — dùng cho biểu đồ tròn.
 *
 * status: 1-đang mượn, 2-đã trả, 3-trả chậm, 4-mất thiết bị
 */
@Data
public class LoanStatusResponse {

  private Long borrowing;   // status = 1
  private Long returned;    // status = 2
  private Long lateReturn;  // status = 3
  private Long lost;        // status = 4
  private Long total;

  public LoanStatusResponse(Tuple tuple) {
    this.borrowing = tuple.get("borrowing", Long.class);
    this.returned = tuple.get("returned", Long.class);
    this.lateReturn = tuple.get("lateReturn", Long.class);
    this.lost = tuple.get("lost", Long.class);
    this.total      = this.borrowing + this.returned + this.lateReturn + this.lost;
  }
}

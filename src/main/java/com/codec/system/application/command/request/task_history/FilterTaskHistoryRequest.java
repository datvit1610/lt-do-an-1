package com.codec.system.application.command.request.task_history;

import lombok.Data;

@Data
public class FilterTaskHistoryRequest {
  private String createdBy; //người thực hiện
  private String task; //tác vụ
  private String startDate; //thực hiện từ ngày
  private String endDate; //đến ngày
  private String content; //nội dung
}

package com.codec.system.application.command.response.task_history;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskHistoryResponse {
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
  private Date createdDate;
  private String createdBy;
  private String task;
  private String content;


  public TaskHistoryResponse(Tuple tuple) {
    this.createdDate = tuple.get("createdDate", Date.class);
    this.createdBy = tuple.get("createdBy", String.class);
    this.task = tuple.get("task", String.class);
    this.content = tuple.get("content", String.class);
  }
}

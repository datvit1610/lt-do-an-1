package com.codec.system.application.command.request.task_history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskHistoryRequest {
  private String task;
  private String content;
  private String userId;
}

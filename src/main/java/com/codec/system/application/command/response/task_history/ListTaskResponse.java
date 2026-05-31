package com.codec.system.application.command.response.task_history;

import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListTaskResponse {
  private String task;


  public ListTaskResponse(Tuple tuple) {
    this.task = tuple.get("task", String.class);
  }
}

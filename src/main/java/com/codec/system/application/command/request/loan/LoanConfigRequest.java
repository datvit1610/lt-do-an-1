package com.codec.system.application.command.request.loan;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanConfigRequest {
  @NotNull(message = "Ngưỡng phút chậm trả không được để trống")
  @Min(value = 0, message = "Ngưỡng phút chậm trả không được âm")
  private Integer lateThresholdMinutes;
}

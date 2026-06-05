package com.codec.system.application.command.request.classperiod;

import lombok.Data;

import java.time.LocalTime;

@Data
public class CreateClassPeriodRequest {
  // Số tiết (1-14)
  private Integer periodNumber;

  // Kíp học: SANG / CHIEU / TOI
  private String shift;

  // Giờ bắt đầu tiết
  private LocalTime startTime;

  // Giờ kết thúc tiết
  private LocalTime endTime;
}

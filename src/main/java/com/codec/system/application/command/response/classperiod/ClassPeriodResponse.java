package com.codec.system.application.command.response.classperiod;

import lombok.Data;

import java.time.LocalTime;
import java.util.Date;

@Data
public class ClassPeriodResponse {
  private String id;
  private Integer periodNumber;
  private String shift;
  private LocalTime startTime;
  private LocalTime endTime;
  private Date createdDate;
  private Date modifiedDate;
}

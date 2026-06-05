package com.codec.system.application.command.response.classperiod;

import lombok.Data;

import java.time.LocalTime;
import java.util.Date;

@Data
public class ClassPeriodResponse {
  private String id;
  private Integer periodNumber; //số thứ tự tiết học
  private String shift; //ca học
  private LocalTime startTime; //thời gian bắt đầu
  private LocalTime endTime;
  private Date createdDate;
  private Date modifiedDate;
}

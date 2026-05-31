package com.codec.system.health.domain.dto;

import lombok.Data;

@Data
public class HealCheckDto {
  private String env;
  private String datasource;
  private String headers;
  private String kafka;
  private Boolean autoflyway;

}

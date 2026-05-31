package com.codec.system.health.application.services.impl;

import codec.common.Response;
import com.codec.system.health.application.services.interfaces.IHealthCheckService;
import com.codec.system.health.domain.dto.HealCheckDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HealthCheckServiceImpl implements IHealthCheckService {


  @Value("${spring.datasource.url}")
  private String datasource;

  @Value("${jhipster.cors.allowed-methods}")
  private String headers;

//  @Value("${spring.kafka.consumer.bootstrap-servers}")
//  private String kafka;

  @Value("${spring.flyway.enabled}")
  private Boolean autoflyway;
  @Value("${spring.profiles.active}")
  private String env;

  @Override
  public Response<HealCheckDto> getProfile() {
    HealCheckDto checkDto=new HealCheckDto();
//    checkDto.setKafka(kafka);
    checkDto.setEnv(env);
    checkDto.setAutoflyway(autoflyway);
    checkDto.setHeaders(headers);
    checkDto.setDatasource(datasource);
    return Response.of(checkDto);
  }
}

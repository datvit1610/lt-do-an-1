package com.codec.system.health.api;

import codec.common.Response;
import com.codec.system.health.application.services.interfaces.IHealthCheckService;
import com.codec.system.health.domain.dto.HealCheckDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/check")
public class ApiHealthCheckController {



  IHealthCheckService healthCheckService;

  public ApiHealthCheckController(IHealthCheckService healthCheckService){
    this.healthCheckService=healthCheckService;
  }

  @GetMapping("/health/ready")
  public Mono<Response<?>> healthReady() {
    return Mono.just(Response.ok());
  }

  @GetMapping("/health/live")
  public Mono<Response<?>> healthLive() {
    return Mono.just(Response.ok());
  }

  @GetMapping("/health/profile")
  public Mono<Response<HealCheckDto>> profile() {
    return Mono.just(healthCheckService.getProfile());
  }
}

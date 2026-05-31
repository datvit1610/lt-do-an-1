package com.codec.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.data.web.ReactiveSortHandlerMethodArgumentResolver;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import tech.jhipster.config.JHipsterProperties;

import java.util.Arrays;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfigurer implements WebFluxConfigurer {

  private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

  private final JHipsterProperties jHipsterProperties;

  public WebConfigurer(JHipsterProperties jHipsterProperties) {
    this.jHipsterProperties = jHipsterProperties;
  }


  @Override
  public void addCorsMappings(CorsRegistry corsRegistry) {
    corsRegistry.addMapping("/**")
      .allowedOrigins("*")
      .maxAge(3600);
  }

//  @Bean
//  public CorsWebFilter corsFilter() {
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    CorsConfiguration config = jHipsterProperties.getCors();
//      config.setAllowedOrigins(Arrays.asList("*"));
//      config.setMaxAge(8000L);
//      config.addAllowedMethod("PUT,POST,GET,OPTIONS,DELETE");
//      config.addAllowedHeader("Codec-Allowed");
//    if (!CollectionUtils.isEmpty(config.getAllowedOrigins()) || !CollectionUtils.isEmpty(config.getAllowedOriginPatterns())) {
//      log.debug("Registering CORS filter");
//      source.registerCorsConfiguration("/api/**", config);
//      source.registerCorsConfiguration("/management/**", config);
//      source.registerCorsConfiguration("/v3/api-docs", config);
//      source.registerCorsConfiguration("/swagger-ui/**", config);
//    }
//    return new CorsWebFilter(source);
//  }

  // TODO: remove when this is supported in spring-boot
  @Bean
  HandlerMethodArgumentResolver reactivePageableHandlerMethodArgumentResolver() {
    return new ReactivePageableHandlerMethodArgumentResolver();
  }

  // TODO: remove when this is supported in spring-boot
  @Bean
  HandlerMethodArgumentResolver reactiveSortHandlerMethodArgumentResolver() {
    return new ReactiveSortHandlerMethodArgumentResolver();
  }

//    @Bean
//    @Order(-2) // The handler must have precedence over WebFluxResponseStatusExceptionHandler and Spring Boot's ErrorWebExceptionHandler
//    public WebExceptionHandler problemExceptionHandler(ObjectMapper mapper, ExceptionTranslator problemHandling) {
//        return new ProblemExceptionHandler(mapper, problemHandling);
//    }
}

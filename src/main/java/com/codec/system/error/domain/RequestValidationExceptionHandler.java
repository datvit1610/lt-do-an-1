package com.codec.system.error.domain;

import codec.common.Response;
import com.codec.system.error.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import jakarta.validation.ConstraintViolationException;

import java.util.Optional;

@Slf4j
@ControllerAdvice
public class RequestValidationExceptionHandler {
  @ExceptionHandler(
    value = {WebExchangeBindException.class}
  )
  protected Mono<ResponseEntity<Response>> handleWebExchangeBindException(
    WebExchangeBindException ex
  ) {
    Response data  = Response.fail(ex.getAllErrors().stream().findFirst().get().getDefaultMessage(), HttpStatus.BAD_REQUEST.value());
    return Mono.just(ResponseEntity.ok().body(data));
  }
  @ExceptionHandler(
    value = {ConstraintViolationException.class}
  )
  protected Mono<ResponseEntity<Response>> handleConstraintViolation(
    ConstraintViolationException ex
  ) {
    Response data  = Response.fail(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    return Mono.just(ResponseEntity.ok().body(data));
  }
  @ExceptionHandler(
    value = {ServerWebInputException.class}
  )
  protected Mono<ResponseEntity<Response>> handleServerWebInput(
    ServerWebInputException ex
  ) {
    log.info(ex.getMessage());
    Response data  = Response.fail("Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST.value());
    return Mono.just(ResponseEntity.ok().body(data));
  }
  @ExceptionHandler(
    value = {Exception.class}
  )
  protected Mono<ResponseEntity<Response>> handleException(
    Exception ex
  ) {
    log.error("Lỗi exception: " + ex.getMessage());
    String message = Optional.ofNullable(ex.getMessage()).orElse("Gặp lỗi trong quá trình xử lý");
    Response data = Response.fail(message, HttpStatus.BAD_REQUEST.value());
    return Mono.just(ResponseEntity.ok().body(data));
  }
  @ExceptionHandler(
    value = {NotFoundException.class}
  )
  protected Mono<ResponseEntity<Response>> handleNotFoundException(
    NotFoundException ex
  ) {
    log.info(ex.getMessage());
    Response data  = Response.fail(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    return Mono.just(ResponseEntity.ok().body(data));
  }

}

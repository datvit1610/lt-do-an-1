package com.codec.system.error.domain;

import codec.error.domain.Assert;
import codec.error.domain.ErrorKey;
import codec.error.domain.ErrorStatus;
import codec.error.domain.StandardErrorKey;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CodecSystemApplicationException extends RuntimeException {

  private final ErrorKey key;
  private final ErrorStatus status;
  private final Map<String, String> parameters;

  protected CodecSystemApplicationException(CodecSystemApplicationExeptionBuilder builder) {
    super(buildMessage(builder), builder.cause);
    key = buildKey(builder);
    status = buildStatus(builder);
    parameters = Collections.unmodifiableMap(builder.parameters);
  }

  private static String buildMessage(CodecSystemApplicationExeptionBuilder builder) {
    Assert.notNull("builder", builder);

    if (builder.message == null) {
      return "An error occured";
    }

    return builder.message;
  }

  private ErrorKey buildKey(CodecSystemApplicationExeptionBuilder builder) {
    if (builder.key == null) {
      return StandardErrorKey.INTERNAL_SERVER_ERROR;
    }

    return builder.key;
  }

  private ErrorStatus buildStatus(CodecSystemApplicationExeptionBuilder builder) {
    if (builder.status == null) {
      return defaultStatus();
    }

    return builder.status;
  }

  private ErrorStatus defaultStatus() {
    return Stream
      .of(Thread.currentThread().getStackTrace())
      .map(StackTraceElement::getClassName)
      .filter(inProject())
      .filter(notCurrentException())
      .findFirst()
      .filter(inPrimary())
      .map(className -> ErrorStatus.BAD_REQUEST)
      .orElse(ErrorStatus.INTERNAL_SERVER_ERROR);
  }

  private Predicate<String> inProject() {
    return className -> className.startsWith("com.codec.system");
  }

  private Predicate<String> notCurrentException() {
    return className -> !className.contains(this.getClass().getName());
  }

  private Predicate<String> inPrimary() {
    return className -> className.contains(".primary");
  }

  public static CodecSystemApplicationExeptionBuilder internalServerError(ErrorKey key) {
    return builder(key).status(ErrorStatus.INTERNAL_SERVER_ERROR);
  }

  public static CodecSystemApplicationExeptionBuilder badRequest(ErrorKey key) {
    return builder(key).status(ErrorStatus.BAD_REQUEST);
  }

  public static CodecSystemApplicationException technicalError(String message) {
    return technicalError(message, null);
  }

  public static CodecSystemApplicationException technicalError(String message, Throwable cause) {
    return builder(StandardErrorKey.INTERNAL_SERVER_ERROR).message(message).cause(cause).build();
  }

  public static CodecSystemApplicationExeptionBuilder builder(ErrorKey key) {
    return new CodecSystemApplicationExeptionBuilder(key);
  }

  public ErrorKey key() {
    return key;
  }

  public ErrorStatus status() {
    return status;
  }

  public Map<String, String> parameters() {
    return parameters;
  }

  public static class CodecSystemApplicationExeptionBuilder {

    private final ErrorKey key;
    private final Map<String, String> parameters = new HashMap<>();

    private String message;
    private Throwable cause;
    private ErrorStatus status;

    private CodecSystemApplicationExeptionBuilder(ErrorKey key) {
      this.key = key;
    }

    public CodecSystemApplicationExeptionBuilder message(String message) {
      this.message = message;

      return this;
    }

    public CodecSystemApplicationExeptionBuilder cause(Throwable cause) {
      this.cause = cause;

      return this;
    }

    public CodecSystemApplicationExeptionBuilder addParameters(Map<String, String> parameters) {
      Assert.notNull("parameters", parameters);

      parameters.forEach(this::addParameter);

      return this;
    }

    public CodecSystemApplicationExeptionBuilder addParameter(String key, String value) {
      Assert.notBlank("key", key);
      Assert.notNull("value", value);

      parameters.put(key, value);

      return this;
    }

    public CodecSystemApplicationExeptionBuilder status(ErrorStatus status) {
      this.status = status;

      return this;
    }

    public CodecSystemApplicationException build() {
      return new CodecSystemApplicationException(this);
    }
  }
}

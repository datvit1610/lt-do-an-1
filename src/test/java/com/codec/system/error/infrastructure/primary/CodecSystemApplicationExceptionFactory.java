package com.codec.system.error.infrastructure.primary;

import com.codec.system.error.domain.CodecSystemApplicationException;

public final class CodecSystemApplicationExceptionFactory {

  private CodecSystemApplicationExceptionFactory() {}

  public static final CodecSystemApplicationException buildEmptyException() {
    return CodecSystemApplicationException.builder(null).build();
  }
}

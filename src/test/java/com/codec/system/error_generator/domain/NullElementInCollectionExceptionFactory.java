package com.codec.system.error_generator.domain;

import codec.error.domain.NullElementInCollectionException;

public final class NullElementInCollectionExceptionFactory {

  private NullElementInCollectionExceptionFactory() {}

  public static NullElementInCollectionException nullElementInCollection() {
    return new NullElementInCollectionException("field");
  }
}

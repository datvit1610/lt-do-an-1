package com.codec.system.common.utils;

import java.util.UUID;

public class Guid {

  public static String createGuid() {
    try {
      String guid = UUID.randomUUID().toString();
      return guid;
    } catch (Exception e) {
      return null;
    }
  }
}

package com.codec.system.common.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Converter
public class LocalTimeUtcConverter implements AttributeConverter<LocalTime, Time> {

  private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
  private static final ZoneId UTC = ZoneId.of("UTC");

  @Override
  public Time convertToDatabaseColumn(LocalTime appTime) {
    if (appTime == null) return null;
    // Convert UTC+7 → UTC trước khi lưu
    LocalTime utcTime = LocalDate.now()
      .atTime(appTime)
      .atZone(APP_ZONE)
      .withZoneSameInstant(UTC)
      .toLocalTime();
    return Time.valueOf(utcTime);
  }

  @Override
  public LocalTime convertToEntityAttribute(Time dbTime) {
    if (dbTime == null) return null;
    // Convert UTC → UTC+7 khi đọc lên
    LocalTime utcTime = dbTime.toLocalTime();
    return LocalDate.now()
      .atTime(utcTime)
      .atZone(UTC)
      .withZoneSameInstant(APP_ZONE)
      .toLocalTime();
  }
}

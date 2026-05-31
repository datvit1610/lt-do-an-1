package com.codec.system.common.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FrequencyConditionGet {
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


  // Lấy danh sách tất cả các ngày thỏa mãn điều kiện trong khoảng thời gian
  public List<LocalDate> getAllValidDates(String conditionType, String conditionValue,
                                          LocalDate startDate, LocalDate endDate) {
    List<LocalDate> validDates = new ArrayList<>();
    LocalDate current = startDate;

    while (!current.isAfter(endDate)) {
      if (isDateSatisfied(conditionType, conditionValue, current)) {
        validDates.add(current);
      }
      current = current.plusDays(1);
    }

    return validDates;
  }

  // Lấy danh sách các ngày thỏa mãn điều kiện từ chuỗi datetime
  public List<LocalDate> getAllValidDates(String conditionType, String conditionValue,
                                          String startDateTime, String endDateTime) {
    LocalDate startDate = parseDateTimeString(startDateTime);
    LocalDate endDate = parseDateTimeString(endDateTime);

    return getAllValidDates(conditionType, conditionValue, startDate, endDate);
  }


  // Parse chuỗi datetime thành LocalDate (bỏ qua thời gian)
  private LocalDate parseDateTimeString(String dateTimeString) {
    try {
      LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
      return dateTime.toLocalDate();
    } catch (Exception e) {
      return LocalDate.parse(dateTimeString.substring(0, 10));
    }
  }


  // Kiểm tra ngày có thỏa mãn điều kiện không (với Date)
  public boolean isDateSatisfied(String conditionType, String conditionValue, Date checkDate) {
    LocalDate date = checkDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    return isDateSatisfied(conditionType, conditionValue, date);
  }


  // Kiểm tra ngày có thỏa mãn điều kiện không (với LocalDate)
  public boolean isDateSatisfied(String conditionType, String conditionValue, LocalDate date) {
    switch (conditionType) {
      case "weekday":
        return validateWeekday(conditionValue, date);
      case "list_day":
        return validateListDay(conditionValue, date);
      case "first_weekday":
        return validateFirstWeekday(conditionValue, date);
      case "last_weekday":
        return validateLastWeekday(conditionValue, date);
      case "date_modulo":
        return validateDateModulo(conditionValue, date);
      case "day_week_month":
        return validateDayWeekMonth(conditionValue, date);
      default:
        throw new IllegalArgumentException("Loại điều kiện không hợp lệ: " + conditionType);
    }
  }

  private boolean validateWeekday(String conditionValue, LocalDate date) {
    int weekday = date.getDayOfWeek().getValue() - 1; // Monday=0, Sunday=6
    return String.valueOf(weekday).equals(conditionValue);
  }

  private boolean validateListDay(String conditionValue, LocalDate date) {
    int weekday = date.getDayOfWeek().getValue() - 1;
    List<Integer> validDays = Arrays.stream(conditionValue.split(","))
      .map(String::trim)
      .map(Integer::parseInt)
      .collect(Collectors.toList());
    return validDays.contains(weekday);
  }

  private boolean validateFirstWeekday(String conditionValue, LocalDate date) {
    int targetWeekday = Integer.parseInt(conditionValue);
    LocalDate firstDay = date.withDayOfMonth(1);

    // Tìm ngày đầu tiên có thứ mong muốn trong tháng
    while (firstDay.getDayOfWeek().getValue() - 1 != targetWeekday) {
      firstDay = firstDay.plusDays(1);
      if (firstDay.getMonth() != date.getMonth()) {
        return false;
      }
    }
    return firstDay.equals(date);
  }

  private boolean validateLastWeekday(String conditionValue, LocalDate date) {
    int targetWeekday = Integer.parseInt(conditionValue);
    LocalDate lastDay = date.withDayOfMonth(date.lengthOfMonth());

    // Tìm ngày cuối cùng có thứ mong muốn trong tháng
    while (lastDay.getDayOfWeek().getValue() - 1 != targetWeekday) {
      lastDay = lastDay.minusDays(1);
      if (lastDay.getMonth() != date.getMonth()) {
        return false;
      }
    }
    return lastDay.equals(date);
  }

  private boolean validateDateModulo(String conditionValue, LocalDate date) {
    int divisor = Integer.parseInt(conditionValue);
    return date.getDayOfMonth() % divisor == 0;
  }

  private boolean validateDayWeekMonth(String conditionValue, LocalDate date) {
    try {
      String[] parts = conditionValue.split("/");
      if (parts.length != 2) return false;

      int targetWeekday = Integer.parseInt(parts[0].trim());
      int targetWeekOfMonth = Integer.parseInt(parts[1].trim());

      int actualWeekday = date.getDayOfWeek().getValue() - 1;
      int actualWeekOfMonth = getWeekOfMonth(date);

      return actualWeekday == targetWeekday && actualWeekOfMonth == targetWeekOfMonth;
    } catch (Exception e) {
      return false;
    }
  }

  private int getWeekOfMonth(LocalDate date) {
    int dayOfMonth = date.getDayOfMonth();
    return (dayOfMonth - 1) / 7 + 1;
  }

  public int countWeeksInMonth(Date date) {
    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    YearMonth yearMonth = YearMonth.from(localDate);
    LocalDate firstDay = yearMonth.atDay(1);
    LocalDate lastDay = yearMonth.atEndOfMonth();

    int firstWeekday = firstDay.getDayOfWeek().getValue();
    int totalDays = lastDay.getDayOfMonth();

    // Tính tổng số tuần
    int offset = firstWeekday - 1; // Đưa về Monday=0
    return ((totalDays + offset - 1) / 7) + 1;
  }
}

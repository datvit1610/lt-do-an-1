package com.codec.system.common.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class FrequencyConditionNameGenerator {

  private static final String[] WEEKDAY_NAMES = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
  private static final String[] WEEKDAY_SHORT_NAMES = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};

  public String generateDisplayName(String conditionType, String conditionValue) {
    if (conditionType == null || conditionValue == null) {
      return "Điều kiện phát sóng";
    }

    switch (conditionType) {
      case "weekday":
        return generateWeekdayName(conditionValue);
      case "list_day":
        return generateListDayName(conditionValue);
//      case "exclude_days": // THÊM CASE MỚI
//        return generateExcludeDaysName(conditionValue);
      case "first_weekday":
        return generateFirstWeekdayName(conditionValue);
      case "last_weekday":
        return generateLastWeekdayName(conditionValue);
      case "date_modulo":
        return generateDateModuloName(conditionValue);
      case "day_week_month":
        return generateDayWeekMonthName(conditionValue);
      default:
        return "Điều kiện phát sóng";
    }
  }

  // THÊM PHƯƠNG THỨC MỚI CHO EXCLUDE_DAYS
//  private String generateExcludeDaysName(String value) {
//    try {
//      String[] dayValues = value.split(",");
//      List<Integer> excludedDays = new ArrayList<>();
//
//      for (String dayStr : dayValues) {
//        int day = Integer.parseInt(dayStr.trim());
//        if (day >= 0 && day <= 6) {
//          excludedDays.add(day);
//        }
//      }
//
//      Collections.sort(excludedDays);
//
//      // Tạo danh sách các ngày được chọn (tất cả trừ những ngày bị loại)
//      List<Integer> includedDays = new ArrayList<>();
//      for (int i = 0; i < 7; i++) {
//        if (!excludedDays.contains(i)) {
//          includedDays.add(i);
//        }
//      }
//
//      // Trường hợp đặc biệt: chỉ trừ 1 ngày
//      if (excludedDays.size() == 1) {
//        int excludedDay = excludedDays.get(0);
//        return "Tất cả các ngày trừ " + WEEKDAY_NAMES[excludedDay].toLowerCase();
//      }
//
//      // Trường hợp: trừ các ngày thường (chỉ phát cuối tuần)
//      if (excludedDays.equals(Arrays.asList(0, 1, 2, 3, 4))) {
//        return "Chỉ cuối tuần";
//      }
//
//      // Trường hợp: trừ cuối tuần (chỉ phát ngày thường)
//      if (excludedDays.equals(Arrays.asList(5, 6))) {
//        return "Chỉ ngày thường";
//      }
//
//      // Trường hợp: trừ nhiều ngày cụ thể
//      if (excludedDays.size() > 0 && excludedDays.size() < 6) {
//        StringBuilder sb = new StringBuilder("Tất cả các ngày trừ ");
//        for (int i = 0; i < excludedDays.size(); i++) {
//          if (i > 0) {
//            if (i == excludedDays.size() - 1) {
//              sb.append(" và ");
//            } else {
//              sb.append(", ");
//            }
//          }
//          sb.append(WEEKDAY_NAMES[excludedDays.get(i)].toLowerCase());
//        }
//        return sb.toString();
//      }
//
//      // Trường hợp: trừ hầu hết các ngày (chỉ chọn vài ngày)
//      if (includedDays.size() > 0 && includedDays.size() < 4) {
//        StringBuilder sb = new StringBuilder("Chỉ ");
//        for (int i = 0; i < includedDays.size(); i++) {
//          if (i > 0) {
//            if (i == includedDays.size() - 1) {
//              sb.append(" và ");
//            } else {
//              sb.append(", ");
//            }
//          }
//          sb.append(WEEKDAY_NAMES[includedDays.get(i)].toLowerCase());
//        }
//        return sb.toString();
//      }
//
//    } catch (Exception e) {
//      // Fall through to default
//    }
//    return "Trừ các ngày chỉ định";
//  }

  // Các phương thức khác giữ nguyên...
  private String generateWeekdayName(String value) {
    try {
      int dayIndex = Integer.parseInt(value.trim());
      if (dayIndex >= 0 && dayIndex < WEEKDAY_NAMES.length) {
        return WEEKDAY_NAMES[dayIndex] + " hàng tuần";
      }
    } catch (NumberFormatException e) {
      // Fall through to default
    }
    return "Ngày trong tuần";
  }

  private String generateListDayName(String value) {
    try {
      String[] dayValues = value.split(",");
      List<Integer> days = new ArrayList<>();

      for (String dayStr : dayValues) {
        int day = Integer.parseInt(dayStr.trim());
        if (day >= 0 && day <= 6) {
          days.add(day);
        }
      }

      Collections.sort(days);

      // Trường hợp đặc biệt: cả tuần
      if (days.size() == 7) {
        return "Cả tuần";
      }

      // Trường hợp đặc biệt: các ngày thường (T2-T6)
      if (days.equals(Arrays.asList(0, 1, 2, 3, 4))) {
        return "Các ngày thường";
      }

      // Trường hợp đặc biệt: cuối tuần
      if (days.equals(Arrays.asList(5, 6))) {
        return "Cuối tuần";
      }

      // Kiểm tra xem có phải là dải liên tục không
      boolean isConsecutive = true;
      for (int i = 1; i < days.size(); i++) {
        if (days.get(i) - days.get(i - 1) != 1) {
          isConsecutive = false;
          break;
        }
      }

      if (isConsecutive && days.size() > 1) {
        // Dải liên tục: "Từ T2-T5"
        return "Từ " + WEEKDAY_SHORT_NAMES[days.get(0)] + "-" + WEEKDAY_SHORT_NAMES[days.get(days.size() - 1)];
      } else {
        // Không liên tục: liệt kê các ngày
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < days.size(); i++) {
          if (i > 0) {
            if (i == days.size() - 1) {
              sb.append(" và ");
            } else {
              sb.append(", ");
            }
          }
          sb.append(WEEKDAY_NAMES[days.get(i)]);
        }
        return sb.toString();
      }

    } catch (Exception e) {
      return "Nhiều ngày";
    }
  }

  private String generateFirstWeekdayName(String value) {
    try {
      int dayIndex = Integer.parseInt(value.trim());
      if (dayIndex >= 0 && dayIndex < WEEKDAY_NAMES.length) {
        return WEEKDAY_NAMES[dayIndex] + " đầu tháng";
      }
    } catch (NumberFormatException e) {
      // Fall through to default
    }
    return "Đầu tháng";
  }

  private String generateLastWeekdayName(String value) {
    try {
      int dayIndex = Integer.parseInt(value.trim());
      if (dayIndex >= 0 && dayIndex < WEEKDAY_NAMES.length) {
        return WEEKDAY_NAMES[dayIndex] + " cuối tháng";
      }
    } catch (NumberFormatException e) {
      // Fall through to default
    }
    return "Cuối tháng";
  }

  private String generateDateModuloName(String value) {
    try {
      int divisor = Integer.parseInt(value.trim());
      if (divisor == 2) {
        return "Ngày chẵn";
      } else if (divisor == 1) {
        return "Hàng ngày";
      } else {
        return "Ngày chia hết cho " + divisor;
      }
    } catch (NumberFormatException e) {
      return "Ngày chia hết";
    }
  }

  private String generateDayWeekMonthName(String value) {
    try {
      String[] parts = value.split("/");
      if (parts.length == 2) {
        int dayOfWeek = Integer.parseInt(parts[0].trim());
        int weekOfMonth = Integer.parseInt(parts[1].trim());

        String weekDescription;
        switch (weekOfMonth) {
          case 1: weekDescription = "đầu"; break;
          case 2: weekDescription = "thứ hai"; break;
          case 3: weekDescription = "thứ ba"; break;
          case 4: weekDescription = "thứ tư"; break;
          case 5: weekDescription = "cuối"; break;
          default: weekDescription = "thứ " + weekOfMonth;
        }

        if (dayOfWeek >= 0 && dayOfWeek < WEEKDAY_NAMES.length) {
          return WEEKDAY_NAMES[dayOfWeek] + " tuần " + weekDescription + " của tháng";
        }
      }
    } catch (Exception e) {
      // Fall through to default
    }
    return "Ngày trong tuần / tuần trong tháng";
  }
}

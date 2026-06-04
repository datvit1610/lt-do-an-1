package com.codec.system.common.utils;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

@Slf4j
public class Utils {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
  private static final DateTimeFormatter TIME_FORMATTER_END_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
  private static final TimeZone GMT_PLUS_7 = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
  private static final ZoneId ZONE_ID = ZoneId.of("Asia/Ho_Chi_Minh");
  private static final ZoneOffset OFFSET = ZoneOffset.ofHours(7);
  private static final String PREFIX = "INV";
  private static final DateTimeFormatter INPUT_FMT = DateTimeFormatter.ofPattern("MM-yyyy");
  private static final DateTimeFormatter OUTPUT_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
  private static final DateTimeFormatter OUTPUT_MONTH = DateTimeFormatter.ofPattern("MM-yyyy");
  public static Date convertStringToDate(String date, String typeFormat) {
    try {
      SimpleDateFormat formatter = new SimpleDateFormat(typeFormat, Locale.ENGLISH);
      return formatter.parse(date);
    } catch (Exception e) {
      return null;
    }
  }
  public static String dateToString(Date date, String typeFormat){
    SimpleDateFormat dateFormat = new SimpleDateFormat(typeFormat);
    TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh"); // Đặt múi giờ thành UTC+7
    dateFormat.setTimeZone(timeZone);
    return dateFormat.format(date);
  }

  public static String localDateToString(LocalTime time, String typeFormat) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(typeFormat);
    return time.format(formatter);
  }

  public static String stringToString(String request) {
    SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date date = null;
    try {
      date = inputDateFormat.parse(request);
    } catch (
      ParseException e) {
      throw new RuntimeException(e);
    }
    String createDate;
    return createDate = outputDateFormat.format(date);
  }

  public static List<String> getGroupIds(String startMonth, String endMonth) {
    int startYearConvert = Integer.parseInt(startMonth.substring(0, 4));
    int startMonthConvert = Integer.parseInt(startMonth.substring(4));
    int endYearConvert = Integer.parseInt(endMonth.substring(0, 4));
    int endMonthConvert = Integer.parseInt(endMonth.substring(4));

    List<String> groupIds = new ArrayList<>();

    for (int year = startYearConvert; year <= endYearConvert; year++) {
      int monthStart = (year == startYearConvert) ? startMonthConvert : 1;
      int monthEnd = (year == endYearConvert) ? endMonthConvert : 12;

      for (int month = monthStart; month <= monthEnd; month++) {
        String formattedDate = String.format("%04d%02d", year, month);
        groupIds.add(formattedDate);
      }
    }
    return groupIds;
  }

  public static List<String> getGroupIdsV2(String startDay, String endDay) {
    int startYearConvert = Integer.parseInt(startDay.substring(0, 4));
    int startMonthConvert = Integer.parseInt(startDay.substring(4, 6));
    int endYearConvert = Integer.parseInt(endDay.substring(0, 4));
    int endMonthConvert = Integer.parseInt(endDay.substring(4, 6));

    List<String> groupIds = new ArrayList<>();

    for (int year = startYearConvert; year <= endYearConvert; year++) {
      int monthStart = (year == startYearConvert) ? startMonthConvert : 1;
      int monthEnd = (year == endYearConvert) ? endMonthConvert : 12;

      for (int month = monthStart; month <= monthEnd; month++) {
        String formattedDate = String.format("%04d%02d", year, month);
        groupIds.add(formattedDate);
      }
    }
    return groupIds;
  }

  public static Date convertStringToDateNoTimeZone(String date, String typeFormat) {
    try {
      SimpleDateFormat formatter = new SimpleDateFormat(typeFormat, Locale.ENGLISH);
      return formatter.parse(date);
    } catch (Exception e) {
      return null;
    }
  }

//  public static String getName(Authentication authentication) {
//    Object principal = authentication.getPrincipal();
//    Object claims = ((Jwt) principal).getClaims().get("name");
//    return claims.toString();
//  }

  public static String creatExportExcelFolderS3(){
    Date newDate = new Date();
    // Chuyển đối tượng Date sang Calendar
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(newDate);
    //Tạo ra thư mục theo năm/tháng/trường
    int monthInt = calendar.get(Calendar.MONTH) + 1;
    String month = "thang-" + monthInt;
    String year = "nam-" + calendar.get(Calendar.YEAR);
    return "export-excel-" + year + "/" + month;
  }

  public static Date convertStartDate(String startDateStr) throws ParseException {
    LocalDate localDate = LocalDate.parse(startDateStr); // Parse dạng "yyyy-MM-dd"
    ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZONE_ID); // 00:00:00 tại GMT+7
    return Date.from(zonedDateTime.toInstant()); // Convert thành java.util.Date
  }

  public static Date convertEndDate(String endDateStr) throws ParseException {
    LocalDate localDate = LocalDate.parse(endDateStr); // Parse chuỗi "yyyy-MM-dd" thành LocalDate
    ZonedDateTime zonedDateTime = localDate.atTime(23, 59, 59).atZone(ZONE_ID); // Set 23:59:59 GMT+7
    return Date.from(zonedDateTime.toInstant()); // Convert thành java.util.Date
  }

  public static LocalTime convertToLocalTime(String timeStr) {
//    log.info("timeStr: " + timeStr);
    // Chuyển đổi chuỗi thời gian thành LocalTime
    LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm:ss"));

    // Chuyển LocalTime thành ZonedDateTime ở múi giờ UTC+7 (Giờ Việt Nam)
//    ZonedDateTime vietnamTime = time.atDate(java.time.LocalDate.now()).atZone(ZoneId.of("Asia/Ho_Chi_Minh"));

    // Chuyển ZonedDateTime thành UTC
//    ZonedDateTime utcTime = vietnamTime.withZoneSameInstant(ZoneId.of("UTC"));

    // Trả về giờ UTC
//    return vietnamTime.toLocalTime();
//    log.info("time: " + time);
    return time;
  }

  public static LocalTime convertToLocalTimeEnd(String timeStr, int durationMedia) {
//    log.info("timeStr end: " + timeStr);
    // Chuyển đổi chuỗi thời gian thành LocalTime
    LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm:ss"));

    // Đặt giây là 0 nếu là thời gian bắt đầu, nếu không thì đặt giây là 59
    time = time.plusSeconds(durationMedia);

    // Chuyển LocalTime thành ZonedDateTime ở múi giờ UTC+7 (Giờ Việt Nam)
//    ZonedDateTime vietnamTime = time.atDate(java.time.LocalDate.now()).atZone(ZoneId.of("Asia/Ho_Chi_Minh"));

    // Chuyển ZonedDateTime thành UTC
//    ZonedDateTime utcTime = vietnamTime.withZoneSameInstant(ZoneId.of("UTC"));

    // Trả về giờ UTC
//    log.info("time end: " + time);
    return time;
  }

  public static LocalTime calculateEndTime(String startTimeBreak, int durationMedia) {
    // Parse String thành LocalTime
    LocalTime startTime = LocalTime.parse(startTimeBreak, TIME_FORMATTER_END_TIME);

    // Cộng thêm số giây
    return startTime.plusSeconds(durationMedia);
  }

  public static String generateInventoryCode(List<String> existingCodes) {
    // Get current date in yyMMdd format
    LocalDate today = LocalDate.now();
    String datePart = today.format(DateTimeFormatter.ofPattern("yyMMdd"));

    // Filter existing codes for today
    int maxSequence = existingCodes.stream()
      .filter(code -> code.startsWith(PREFIX + datePart))
      .mapToInt(code -> Integer.parseInt(code.substring(9))) // Extract NNNN
      .max()
      .orElse(0);

    // Next sequence number
    int nextSequence = maxSequence + 1;
    if (nextSequence > 9999) {
      throw new RuntimeException("Inventory code limit exceeded for today");
    }

    String sequencePart = String.format("%04d", nextSequence);
    return PREFIX + datePart + sequencePart;
  }

  public static Date getStartDate (String month) {
    // Chuẩn hóa: thay "/" bằng "-" để thống nhất format
    month = month.replace("/", "-");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
    YearMonth ym = YearMonth.parse(month, formatter);
    // Lấy ngày đầu tiên 00:00:00
    LocalDateTime startOfMonth = ym.atDay(1).atStartOfDay();
    // Chuyển sang java.util.Date
    ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");
    return Date.from(startOfMonth.atZone(zone).toInstant());
  }

  public static Date getEndDate (String month) {
    // Chuẩn hóa: thay "/" bằng "-" để thống nhất format
    month = month.replace("/", "-");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
    YearMonth ym = YearMonth.parse(month, formatter);
    // Lấy ngày đầu tiên 00:00:00
    LocalDateTime endOfMonth = ym.atEndOfMonth().atTime(23, 59, 59);
    // Chuyển sang java.util.Date
    ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");
    return Date.from(endOfMonth.atZone(zone).toInstant());
  }

  /**
   * Trả về danh sách String "dd/MM" cho tất cả ngày trong tháng.
   */
  public static List<String> getDatesForMonth(String mmyyyy) {
    mmyyyy = mmyyyy.replace("/", "-");
    YearMonth ym = YearMonth.parse(mmyyyy, INPUT_FMT);
    LocalDate start = ym.atDay(1);
    LocalDate end = ym.atEndOfMonth();

    List<String> result = new ArrayList<>();
    LocalDate d = start;
    while (!d.isAfter(end)) {
      result.add(d.format(OUTPUT_FMT));
      d = d.plusDays(1);
    }
    return result;
  }

  /**
   * Trả về danh sách String "dd/MM" cho tất cả ngày trong tuần hiện tại,
   * nhưng chỉ giữ những ngày thuộc tháng/năm được chọn.
   */
  public static List<String> getDatesForCurrentWeekInsideMonth(String mmyyyy) {
    mmyyyy = mmyyyy.replace("/", "-");
    YearMonth target = YearMonth.parse(mmyyyy, INPUT_FMT);
    LocalDate today = LocalDate.now(ZONE_ID);

    WeekFields wf = WeekFields.ISO; // Tuần bắt đầu từ Thứ Hai
    LocalDate weekStart = today.with(wf.dayOfWeek(), 1);
    LocalDate weekEnd = today.with(wf.dayOfWeek(), 7);

    List<String> result = new ArrayList<>();
    LocalDate d = weekStart;
    while (!d.isAfter(weekEnd)) {
      if (YearMonth.from(d).equals(target)) {
        result.add(d.format(OUTPUT_FMT));
      }
      d = d.plusDays(1);
    }
    return result;
  }

  public static String getTimeByStartAndEnd(Long startTime, Long endTime){
    // Tính toán sự chênh lệch
    long elapsedTime = endTime - startTime;
    double seconds = (double) elapsedTime;
    return String.valueOf(seconds);
  }

  /**
   * Trả về danh sách các tháng dạng "MM/yyyy" bắt đầu từ mmyyyy, gồm count tháng (count >= 1).
   * Ví dụ: getMonths("10/2025", 3) -> ["10/2025", "11/2025", "12/2025"]
   */
  public static List<String> getMonths(String mmyyyy, int count) {
    mmyyyy = mmyyyy.replace("/", "-");
    if (count < 1) throw new IllegalArgumentException("count must be >= 1");
    YearMonth start = YearMonth.parse(mmyyyy, INPUT_FMT);

    List<String> result = new ArrayList<>(count);
    YearMonth cur = start;
    for (int i = 0; i < count; i++) {
      result.add(cur.format(OUTPUT_MONTH));
      cur = cur.plusMonths(1);
    }
    return result;
  }

  public static LocalDate convertDateToLocalDate(Date date) {
    return date.toInstant()
      .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
      .toLocalDate();
  }

  public static String convertSeconds(int seconds) {
    int minutes = seconds / 60;
    int remainSeconds = seconds % 60;
    return minutes + "p" + (remainSeconds != 0 ? (remainSeconds + "s") : "");
  }

  public static String convert(String input) {
    DateTimeFormatter from = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    DateTimeFormatter to = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    return LocalDate.parse(input, from).format(to);
  }

  public static LocalTime fromUtcTime(LocalTime utcTime) {
    return LocalDate.now()
      .atTime(utcTime)
      .atZone(ZoneId.of("UTC"))
      .withZoneSameInstant(ZoneId.of("Asia/Ho_Chi_Minh"))
      .toLocalTime();
  }

  public static String calculateDaysUntilExpiry(LocalDate expiryDate) {
    if (expiryDate == null) {
      return "Vô thời hạn";
    }
    LocalDate today = LocalDate.now();
    // Hết hạn
    if (expiryDate.isBefore(today)) {
      return "Hết hạn";
    }
    Period period = Period.between(today, expiryDate);
    int years = period.getYears();
    int months = period.getMonths();
    int days = period.getDays();
    StringBuilder result = new StringBuilder();
    if (years > 0) {
      result.append(years).append(" năm ");
    }
    if (months > 0) {
      result.append(months).append(" tháng ");
    }
    if (days > 0) {
      result.append(days).append(" ngày");
    }
    return result.toString().trim();
  }

}

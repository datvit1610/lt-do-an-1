//package com.codec.system.config;
//
//import com.codec.system.domain.entity.ClassPeriodEntity;
//import com.codec.system.domain.repository.ClassPeriodRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Seed masterdata ca học (14 tiết) theo khung giờ lên lớp khi bảng class_periods còn rỗng.
// */
////@Component
//@Order(1)
//@Slf4j
//@RequiredArgsConstructor
//public class ClassPeriodSeeder implements CommandLineRunner {
//
//  private final ClassPeriodRepository classPeriodRepository;
//
//  @Override
//  @Transactional
//  public void run(String... args) {
//    if (classPeriodRepository.count() > 0) {
//      return;
//    }
//
//    List<ClassPeriodEntity> periods = new ArrayList<>();
//    // KÍP SÁNG
//    periods.add(build(1, "SANG", LocalTime.of(6, 45), LocalTime.of(7, 30)));
//    periods.add(build(2, "SANG", LocalTime.of(7, 30), LocalTime.of(8, 15)));
//    periods.add(build(3, "SANG", LocalTime.of(8, 25), LocalTime.of(9, 10)));
//    periods.add(build(4, "SANG", LocalTime.of(9, 20), LocalTime.of(10, 5)));
//    periods.add(build(5, "SANG", LocalTime.of(10, 15), LocalTime.of(11, 0)));
//    periods.add(build(6, "SANG", LocalTime.of(11, 0), LocalTime.of(11, 45)));
//    // KÍP CHIỀU
//    periods.add(build(7, "CHIEU", LocalTime.of(12, 30), LocalTime.of(13, 15)));
//    periods.add(build(8, "CHIEU", LocalTime.of(13, 15), LocalTime.of(14, 0)));
//    periods.add(build(9, "CHIEU", LocalTime.of(14, 10), LocalTime.of(14, 55)));
//    periods.add(build(10, "CHIEU", LocalTime.of(15, 5), LocalTime.of(15, 50)));
//    periods.add(build(11, "CHIEU", LocalTime.of(16, 0), LocalTime.of(16, 45)));
//    periods.add(build(12, "CHIEU", LocalTime.of(16, 45), LocalTime.of(17, 30)));
//    // KÍP TỐI
//    periods.add(build(13, "TOI", LocalTime.of(17, 45), LocalTime.of(18, 30)));
//    periods.add(build(14, "TOI", LocalTime.of(18, 30), LocalTime.of(19, 15)));
//
//    classPeriodRepository.saveAll(periods);
//    log.info("Đã seed {} tiết học vào masterdata class_periods", periods.size());
//  }
//
//  private ClassPeriodEntity build(int number, String shift, LocalTime start, LocalTime end) {
//    ClassPeriodEntity entity = new ClassPeriodEntity();
//    entity.setPeriodNumber(number);
//    entity.setShift(shift);
//    entity.setStartTime(start);
//    entity.setEndTime(end);
//    entity.setCreatedBy("system");
//    return entity;
//  }
//}

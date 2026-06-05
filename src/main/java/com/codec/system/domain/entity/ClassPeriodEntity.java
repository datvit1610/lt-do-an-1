package com.codec.system.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;

import java.time.LocalTime;

@Getter
@Setter
@Table(name = "class_periods")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassPeriodEntity extends BaseEntity {

  @Comment("Số tiết (1-14)")
  @Column(name = "period_number", nullable = false, unique = true)
  Integer periodNumber;

  @Comment("Kíp học: SANG - sáng, CHIEU - chiều, TOI - tối")
  @Column(name = "shift")
  String shift;

  @Comment("Giờ bắt đầu tiết")
  @Column(name = "start_time", columnDefinition = "time")
  LocalTime startTime;

  @Comment("Giờ kết thúc tiết")
  @Column(name = "end_time", columnDefinition = "time")
  LocalTime endTime;

}

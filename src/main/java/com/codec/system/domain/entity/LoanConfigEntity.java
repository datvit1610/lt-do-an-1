package com.codec.system.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Table(name = "loan_config")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanConfigEntity extends BaseEntity {

  @Comment("Số phút quá giờ kết thúc tiết cần trả thì bị đánh chậm trả, mặc định 15")
  @Column(name = "late_threshold_minutes")
  Integer lateThresholdMinutes = 15;
}

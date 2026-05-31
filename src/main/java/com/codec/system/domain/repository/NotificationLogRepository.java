package com.codec.system.domain.repository;

import com.codec.system.domain.entity.NotificationLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, Long> {

  // Lấy bản ghi chưa gửi gần nhất
  NotificationLogEntity findFirstBySentAtIsNullOrderByIdDesc();

  @Query("""
        SELECT n FROM NotificationLogEntity n
        WHERE n.sentAt IS NOT NULL
          AND (:from IS NULL OR CAST(n.sentAt AS date) >= :from)
          AND (:to   IS NULL OR CAST(n.sentAt AS date) <= :to)
        ORDER BY n.sentAt DESC
        """)
  Page<NotificationLogEntity> searchByDateRange(
    @Param("from") LocalDate from,
    @Param("to")   LocalDate to,
    Pageable pageable);
}

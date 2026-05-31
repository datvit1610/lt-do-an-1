package com.codec.system.domain.repository;

import com.codec.system.domain.entity.NotificationUserEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationUserRepository extends JpaRepository<NotificationUserEntity, Long> {

  // Repository
  @Query(value = """
    select nu.notification_log_id AS id,
            nl.content AS content,
            nl.sent_at AS sentAt,
            nu.is_read AS isRead,
            nu.read_at AS readAt
    FROM notification_log nl
    JOIN notification_user nu ON nu.notification_log_id = nl.id
    WHERE nu.user_id = :userId
      AND nl.sent_at IS NOT NULL
      AND (CAST(:from AS timestamp) IS NULL OR nl.sent_at >= CAST(:from AS timestamp))
      AND (CAST(:to   AS timestamp) IS NULL OR nl.sent_at <= CAST(:to   AS timestamp))
    ORDER BY nl.sent_at DESC
    """,
    countQuery = """
    SELECT COUNT(*)
    FROM notification_log nl
    JOIN notification_user nu ON nu.notification_log_id = nl.id
    WHERE nu.user_id = :userId
      AND nl.sent_at IS NOT NULL
      AND (CAST(:from AS timestamp) IS NULL OR nl.sent_at >= CAST(:from AS timestamp))
      AND (CAST(:to   AS timestamp) IS NULL OR nl.sent_at <= CAST(:to   AS timestamp))
    """,
    nativeQuery = true)
  Page<Tuple> findByUserId(
    @Param("userId") String userId,
    @Param("from") LocalDateTime from,
    @Param("to") LocalDateTime to,
    Pageable pageable);

  Optional<NotificationUserEntity> findFirstByNotificationLogIdAndUserId(
    String notificationLogId, String userId);

  List<NotificationUserEntity> findByUserIdAndIsRead(String userId, Boolean isRead);

  long countByUserIdAndIsRead(String userId, Boolean isRead);
}

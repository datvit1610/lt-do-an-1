package com.codec.system.domain.repository;

import com.codec.system.domain.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, String> {

  /**
   * Đếm số thông báo chưa đọc của một người dùng (is_read = false, deleted = false).
   */
  @Query("""
      SELECT COUNT(n)
      FROM NotificationEntity n
      WHERE n.isRead = false
        AND n.deleted = false
        AND n.userId = :userId
      """)
  long countUnread(@Param("userId") String userId);

  /**
   * Danh sách thông báo của một người dùng, chưa xóa, sắp xếp theo thời gian tạo giảm dần.
   */
  Page<NotificationEntity> findByUserIdAndDeletedFalseOrderByCreatedDateDesc(String userId, Pageable pageable);

  /**
   * Đánh dấu tất cả thông báo (chưa đọc, chưa xóa) của một người dùng là đã đọc.
   */
  @Modifying
  @Query("""
      UPDATE NotificationEntity n
      SET n.isRead = true
      WHERE n.isRead = false
        AND n.deleted = false
        AND n.userId = :userId
      """)
  int markAllRead(@Param("userId") String userId);

  /**
   * Đánh dấu một thông báo theo id là đã đọc.
   */
  @Modifying
  @Query("""
      UPDATE NotificationEntity n
      SET n.isRead = true
      WHERE n.id = :id
      """)
  int markOneRead(@Param("id") String id);

  /**
   * Kiểm tra đã có thông báo theo ref_id + user_id chưa (tránh insert trùng).
   */
  @Query("""
      SELECT COUNT(n) > 0
      FROM NotificationEntity n
      WHERE n.refId = :refId
        AND n.userId = :userId
      """)
  boolean checkDuplicate(@Param("refId") String refId, @Param("userId") String userId);
}

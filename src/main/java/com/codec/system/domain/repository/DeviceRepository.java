package com.codec.system.domain.repository;

import com.codec.system.application.command.response.device.DeviceOptionResponse;
import com.codec.system.domain.entity.DeviceEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, String> {
  Optional<DeviceEntity> findByDeviceCodeAndDeletedIsFalse(String deviceCode);

  @Query("""
    select d.id as id, d.name as name
    from DeviceEntity d
    where d.deleted = false and d.status = 1
    and (:name is null or :name = '' or lower(d.name) like lower(concat('%', :name, '%')))
    order by d.name asc
  """)
  List<Tuple> findDeviceOptions(@Param("name") String name);

  @Query(value = "SELECT d.id, d.name, d.device_code, d.device_type, d.status, d.location, " +
      "d.quantity, d.description, d.created_date, COALESCE(u_creator.full_name, d.created_by) as created_by " +
      "FROM devices d " +
      "LEFT JOIN users u_creator ON d.created_by = u_creator.id " +
      "WHERE d.deleted = false " +
      "AND (:deviceCode IS NULL OR LOWER(d.device_code) LIKE LOWER(CONCAT('%', :deviceCode, '%'))) " +
      "AND (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
      "AND (:deviceType IS NULL OR LOWER(d.device_type) LIKE LOWER(CONCAT('%', :deviceType, '%'))) " +
      "AND (:status IS NULL OR d.status = :status) " +
      "ORDER BY d.created_date DESC",
      countQuery = "SELECT COUNT(*) FROM devices d " +
          "WHERE d.deleted = false " +
          "AND (:deviceCode IS NULL OR LOWER(d.device_code) LIKE LOWER(CONCAT('%', :deviceCode, '%'))) " +
          "AND (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
          "AND (:deviceType IS NULL OR LOWER(d.device_type) LIKE LOWER(CONCAT('%', :deviceType, '%'))) " +
          "AND (:status IS NULL OR d.status = :status)",
      nativeQuery = true)
  Page<Object[]> findAllDevicesWithUserNames(
      Pageable pageable,
      @Param("deviceCode") String deviceCode,
      @Param("name") String name,
      @Param("deviceType") String deviceType,
      @Param("status") Integer status
  );

  /**
   * Đầu thiết bị: đếm số dòng thiết bị đang hoạt động, chưa xóa.
   */
  @Query("SELECT COUNT(d) FROM DeviceEntity d WHERE d.status = 1 AND d.deleted = false")
  Long countActiveDevices();

  /**
   * Tổng số lượng: cộng dồn quantity của thiết bị đang hoạt động, chưa xóa.
   * Dùng COALESCE để tránh NULL khi bảng trống.
   */
  @Query("SELECT COALESCE(SUM(d.quantity), 0) FROM DeviceEntity d WHERE d.status = 1 AND d.deleted = false")
  Long sumActiveDeviceQuantity();

}

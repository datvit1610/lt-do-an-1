package com.codec.system.domain.repository;

import com.codec.system.domain.entity.DeviceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, String> {
  Optional<DeviceEntity> findByDeviceCodeAndDeletedIsFalse(String deviceCode);

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
      @org.springframework.data.repository.query.Param("deviceCode") String deviceCode,
      @org.springframework.data.repository.query.Param("name") String name,
      @org.springframework.data.repository.query.Param("deviceType") String deviceType,
      @org.springframework.data.repository.query.Param("status") Integer status
  );

}

package com.codec.system.domain.repository;

import com.codec.system.domain.entity.DeviceTypeEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceTypeRepository extends JpaRepository<DeviceTypeEntity, String> {

  boolean existsByDeviceTypeAndDeletedIsFalse(String deviceType);

  /**
   * Danh sách loại thiết bị rút gọn (id, deviceType) để chọn lúc tạo thiết bị.
   */
  @Query("""
    select dt.id as id, dt.deviceType as deviceType
    from DeviceTypeEntity dt
    where dt.deleted = false
    and (:name is null or :name = '' or lower(dt.deviceType) like lower(concat('%', :name, '%')))
    order by dt.deviceType asc
  """)
  List<Tuple> findDeviceTypeOptions(@Param("name") String name);

  @Query(value = "SELECT dt.id, dt.device_type " +
      "FROM device_type dt " +
      "WHERE dt.deleted = false " +
      "AND (:name IS NULL OR LOWER(dt.device_type) LIKE LOWER(CONCAT('%', :name, '%'))) " +
      "ORDER BY dt.created_date DESC",
      countQuery = "SELECT COUNT(*) FROM device_type dt " +
          "WHERE dt.deleted = false " +
          "AND (:name IS NULL OR LOWER(dt.device_type) LIKE LOWER(CONCAT('%', :name, '%')))",
      nativeQuery = true)
  Page<Object[]> findAllDeviceTypes(
      Pageable pageable,
      @Param("name") String name
  );
}

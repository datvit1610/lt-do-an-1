package com.codec.system.domain.repository;

import com.codec.system.domain.entity.ExportHistoryEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExportHistoryRepository extends JpaRepository<ExportHistoryEntity, String> {

  //check sản phẩm đã có lịch sử xuất kho chưa
  boolean existsByWarehouseId(String warehouseId);
  @Query("""
    SELECT
        eh.id AS historyId,
        w.id AS warehouseId,
        eh.exportDate AS exportDate,
        w.productGroup AS productGroup,
        w.productName AS productName,
        w.productDetail AS productDetail,
        w.mixingSpecification AS mixingSpecification,
        w.importDate AS importDate,
        w.unit AS unit,
        eh.exportQuantity AS exportQuantity,
        u.fullName AS exportedBy,
        w.expiryDate AS expiryDate,
        w.storageLocation AS storageLocation,
        w.origin AS origin

    FROM ExportHistoryEntity eh

    JOIN WarehouseEntity w
        ON eh.warehouseId = w.id

    LEFT JOIN UserEntity u
        ON eh.exportedBy = u.id

    WHERE w.deleted = false

      AND (
            CAST(:exportDateFrom AS date) IS NULL
            OR DATE(eh.exportDate) >= :exportDateFrom
      )

      AND (
            CAST(:exportDateTo AS date) IS NULL
            OR DATE(eh.exportDate) <= :exportDateTo
      )
      AND (
            :productGroup IS NULL
            OR LOWER(w.productGroup)
            LIKE LOWER(CONCAT('%', :productGroup, '%'))
      )
      AND (
            :productName IS NULL
            OR LOWER(w.productName)
            LIKE LOWER(CONCAT('%', :productName, '%'))
      )
      AND (
            :exportedBy IS NULL
            OR LOWER(u.fullName)
            LIKE LOWER(CONCAT('%', :exportedBy, '%'))
      )
      AND (
            :storageLocation IS NULL
            OR LOWER(w.storageLocation)
            LIKE LOWER(CONCAT('%', :storageLocation, '%'))
      )

    ORDER BY eh.exportDate DESC
""")
  Page<Tuple> searchExportHistory(
    @Param("exportDateFrom") LocalDate exportDateFrom,
    @Param("exportDateTo") LocalDate exportDateTo,
    @Param("productGroup") String productGroup,
    @Param("productName") String productName,
    @Param("exportedBy") String exportedBy,
    @Param("storageLocation") String storageLocation,

    Pageable pageable
  );

  @Query("""
    SELECT
        eh.id AS historyId,
        w.id AS warehouseId,
        eh.exportDate AS exportDate,
        w.productGroup AS productGroup,
        w.productName AS productName,
        w.productDetail AS productDetail,
        w.mixingSpecification AS mixingSpecification,
        w.importDate AS importDate,
        w.unit AS unit,
        eh.exportQuantity AS exportQuantity,
        u.fullName AS exportedBy,
        w.expiryDate AS expiryDate,
        w.storageLocation AS storageLocation,
        w.origin AS origin

    FROM ExportHistoryEntity eh

    JOIN WarehouseEntity w
        ON eh.warehouseId = w.id

    LEFT JOIN UserEntity u
        ON eh.exportedBy = u.id

    WHERE w.deleted = false

      AND (
            CAST(:exportDateFrom AS date) IS NULL
            OR DATE(eh.exportDate) >= :exportDateFrom
      )

      AND (
            CAST(:exportDateTo AS date) IS NULL
            OR DATE(eh.exportDate) <= :exportDateTo
      )
      AND (
            :productGroup IS NULL
            OR LOWER(w.productGroup)
            LIKE LOWER(CONCAT('%', :productGroup, '%'))
      )
      AND (
            :productName IS NULL
            OR LOWER(w.productName)
            LIKE LOWER(CONCAT('%', :productName, '%'))
      )
      AND (
            :exportedBy IS NULL
            OR LOWER(u.fullName)
            LIKE LOWER(CONCAT('%', :exportedBy, '%'))
      )
      AND (
            :storageLocation IS NULL
            OR LOWER(w.storageLocation)
            LIKE LOWER(CONCAT('%', :storageLocation, '%'))
      )

    ORDER BY eh.exportDate DESC
""")
  List<Tuple> exportHistory(
    @Param("exportDateFrom") LocalDate exportDateFrom,
    @Param("exportDateTo") LocalDate exportDateTo,
    @Param("productGroup") String productGroup,
    @Param("productName") String productName,
    @Param("exportedBy") String exportedBy,
    @Param("storageLocation") String storageLocation
  );
}

package com.codec.system.domain.repository;

import com.codec.system.domain.entity.WarehouseEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, String> {

  List<WarehouseEntity> findByIdInAndDeletedFalse(List<String> ids);
  Optional<WarehouseEntity> findByIdAndDeletedFalse(String id);

  boolean existsByProductGroupAndProductNameAndUnitAndManufacturingDateAndExpiryDateAndDeletedFalse(String productGroup, String productName, String unit, LocalDate manufacturingDate, LocalDate expiryDate);

  @Query("""
        SELECT COUNT(w) > 0
        FROM WarehouseEntity w
        WHERE LOWER(TRIM(w.productGroup)) = LOWER(TRIM(:productGroup))
          AND LOWER(TRIM(w.productName)) = LOWER(TRIM(:productName))
          AND LOWER(TRIM(w.unit)) = LOWER(TRIM(:unit))
          AND w.id <> :id
          AND w.deleted = false
    """)
  boolean existsWarehouseForUpdate(
    @Param("productGroup") String productGroup,
    @Param("productName") String productName,
    @Param("unit") String unit,
    @Param("id") String id
  );

  @Query("""
    SELECT w.id AS id,
           w.productGroup AS productGroup,
           w.productName AS productName,
           w.origin AS origin,
           w.productDetail AS productDetail,
           w.mixingSpecification AS mixingSpecification,
           w.manufacturingDate AS manufacturingDate,
           w.storageLocation AS storageLocation,
           w.importDate AS importDate,
           w.expiryDate AS expiryDate,
           w.unit AS unit,
           w.importQuantity AS importQuantity,
           w.exportQuantity AS exportQuantity,
           (w.importQuantity - w.exportQuantity) AS remainingQuantity,
           CASE WHEN (w.importQuantity - w.exportQuantity) > 0 THEN 1 ELSE 2 END AS stockStatus,
           w.importedBy AS importedBy,
           w.note AS note,
           w.qrCode AS qrCode,
           CASE
               WHEN w.expiryDate IS NULL THEN 4
               WHEN w.manufacturingDate IS NULL THEN 4
               WHEN w.expiryDate < :today THEN 3
               WHEN w.expiryDate <= :thresholdDate THEN 2
               ELSE 1
           END AS status
    FROM WarehouseEntity w
    WHERE (:productGroup  IS NULL OR LOWER(w.productGroup)      LIKE LOWER(CONCAT('%', :productGroup,      '%')))
      AND (:productName  IS NULL OR LOWER(w.productName)      LIKE LOWER(CONCAT('%', :productName,      '%')))
      AND (:origin       IS NULL OR LOWER(w.origin)           LIKE LOWER(CONCAT('%', :origin,           '%')))
      AND (:productDetail IS NULL OR LOWER(w.productDetail)   LIKE LOWER(CONCAT('%', :productDetail,    '%')))
      AND (:storageLocation IS NULL OR LOWER(w.storageLocation) LIKE LOWER(CONCAT('%', :storageLocation, '%')))
      AND (CAST(:importDateFrom AS date) IS NULL OR w.importDate >= :importDateFrom)
      AND (CAST(:importDateTo   AS date) IS NULL OR w.importDate <= :importDateTo)
      AND (CAST(:expiryDateFrom AS date) IS NULL OR w.expiryDate >= :expiryDateFrom)
      AND (CAST(:expiryDateTo   AS date) IS NULL OR w.expiryDate <= :expiryDateTo)
      AND (
          :status IS NULL
          OR (
              (
                  (:status = 1 AND (w.expiryDate IS NULL OR w.expiryDate > :thresholdDate))
                  OR (:status = 2 AND w.expiryDate >= :today AND w.expiryDate <= :thresholdDate)
                  OR (:status = 3 AND w.expiryDate < :today)
                  OR (:status = 4 AND (w.expiryDate IS NULL OR w.manufacturingDate IS NULL))
              )
              AND (w.importQuantity - w.exportQuantity) > 0
          )
      )
      AND (
          :stockStatus IS NULL
          OR (:stockStatus = 1 AND (w.importQuantity - w.exportQuantity) > 0)
          OR (:stockStatus = 2 AND (w.importQuantity - w.exportQuantity) <= 0)
      )
      AND (
          :qrStatus IS NULL
          OR (:qrStatus = 1 AND w.qrCode IS NULL)
          OR (:qrStatus = 2 AND w.qrCode IS NOT NULL)
      )
      AND w.deleted = false
    ORDER BY w.importDate DESC
    """)
  Page<Tuple> searchWarehouse(
    @Param("productGroup")    String productGroup,
    @Param("productName")     String productName,
    @Param("origin")          String origin,
    @Param("productDetail")   String productDetail,
    @Param("storageLocation") String storageLocation,
    @Param("importDateFrom")  LocalDate importDateFrom,
    @Param("importDateTo")    LocalDate importDateTo,
    @Param("expiryDateFrom")  LocalDate expiryDateFrom,
    @Param("expiryDateTo")    LocalDate expiryDateTo,
    @Param("status")          Integer status,
    @Param("stockStatus")     Integer stockStatus,
    @Param("qrStatus")        Integer qrStatus,
    @Param("today")           LocalDate today,
    @Param("thresholdDate")   LocalDate thresholdDate,
    Pageable pageable
  );


  //chỉ lấy còn hàng, còn lại lấy hết
  @Query("""
    SELECT w.id AS id,
           w.productGroup AS productGroup,
           w.productName AS productName,
           w.origin AS origin,
           w.productDetail AS productDetail,
           w.mixingSpecification AS mixingSpecification,
           w.manufacturingDate AS manufacturingDate,
           w.storageLocation AS storageLocation,
           w.importDate AS importDate,
           w.expiryDate AS expiryDate,
           w.unit AS unit,
           w.importQuantity AS importQuantity,
           w.exportQuantity AS exportQuantity,
           (w.importQuantity - w.exportQuantity) AS remainingQuantity,
           CASE WHEN (w.importQuantity - w.exportQuantity) > 0 THEN 1 ELSE 2 END AS stockStatus,
           w.importedBy AS importedBy,
           w.note AS note,
           w.qrCode AS qrCode,
           CASE
               WHEN w.expiryDate < :today THEN 3
               WHEN w.expiryDate <= :thresholdDate THEN 2
               WHEN w.expiryDate IS NULL THEN 4
               WHEN w.manufacturingDate IS NULL THEN 4
               ELSE 1
           END AS status
    FROM WarehouseEntity w
    WHERE (:productGroup  IS NULL OR LOWER(w.productGroup)      LIKE LOWER(CONCAT('%', :productGroup,      '%')))
      AND (:productName  IS NULL OR LOWER(w.productName)      LIKE LOWER(CONCAT('%', :productName,      '%')))
      AND (:origin       IS NULL OR LOWER(w.origin)           LIKE LOWER(CONCAT('%', :origin,           '%')))
      AND (:storageLocation IS NULL OR LOWER(w.storageLocation) LIKE LOWER(CONCAT('%', :storageLocation, '%')))
      AND ((w.importQuantity - w.exportQuantity) > 0)
      AND w.deleted = false
      ORDER BY w.expiryDate ASC
    """)
  Page<Tuple> searchForExportWarehouse(
    @Param("productGroup")    String productGroup,
    @Param("productName")     String productName,
    @Param("origin")          String origin,
    @Param("storageLocation") String storageLocation,
    @Param("today")           LocalDate today,
    @Param("thresholdDate")   LocalDate thresholdDate,
    Pageable pageable
  );

  @Query("""
    SELECT w.id AS id,
           w.productGroup AS productGroup,
           w.productName AS productName,
           w.origin AS origin,
           w.productDetail AS productDetail,
           w.mixingSpecification AS mixingSpecification,
           w.manufacturingDate AS manufacturingDate,
           w.storageLocation AS storageLocation,
           w.importDate AS importDate,
           w.expiryDate AS expiryDate,
           w.unit AS unit,
           w.importQuantity AS importQuantity,
           w.exportQuantity AS exportQuantity,
           (w.importQuantity - w.exportQuantity) AS remainingQuantity,
           CASE WHEN (w.importQuantity - w.exportQuantity) > 0 THEN 1 ELSE 2 END AS stockStatus,
           w.importedBy AS importedBy,
           w.note AS note,
           w.qrCode AS qrCode,
           CASE
               WHEN w.expiryDate < :today THEN 3
               WHEN w.expiryDate <= :thresholdDate THEN 2
                WHEN w.expiryDate IS NULL THEN 4
                WHEN w.manufacturingDate IS NULL THEN 4
               ELSE 1
           END AS status
    FROM WarehouseEntity w
    WHERE (:productGroup  IS NULL OR LOWER(w.productGroup)      LIKE LOWER(CONCAT('%', :productGroup,      '%')))
      AND (:productName  IS NULL OR LOWER(w.productName)      LIKE LOWER(CONCAT('%', :productName,      '%')))
      AND (:origin       IS NULL OR LOWER(w.origin)           LIKE LOWER(CONCAT('%', :origin,           '%')))
      AND (:productDetail IS NULL OR LOWER(w.productDetail)   LIKE LOWER(CONCAT('%', :productDetail,    '%')))
      AND (:storageLocation IS NULL OR LOWER(w.storageLocation) LIKE LOWER(CONCAT('%', :storageLocation, '%')))
      AND (CAST(:importDateFrom AS date) IS NULL OR w.importDate >= :importDateFrom)
      AND (CAST(:importDateTo   AS date) IS NULL OR w.importDate <= :importDateTo)
      AND (CAST(:expiryDateFrom AS date) IS NULL OR w.expiryDate >= :expiryDateFrom)
      AND (CAST(:expiryDateTo   AS date) IS NULL OR w.expiryDate <= :expiryDateTo)
      AND (
          :status IS NULL
          OR (
              (
                  (:status = 1 AND (w.expiryDate IS NULL OR w.expiryDate > :thresholdDate))
                  OR (:status = 2 AND w.expiryDate >= :today AND w.expiryDate <= :thresholdDate)
                  OR (:status = 3 AND w.expiryDate < :today)
                  OR (:status = 4 AND (w.expiryDate IS NULL OR w.manufacturingDate IS NULL))
              )
              AND (w.importQuantity - w.exportQuantity) > 0
          )
      )
      AND (
          :stockStatus IS NULL
          OR (:stockStatus = 1 AND (w.importQuantity - w.exportQuantity) > 0)
          OR (:stockStatus = 2 AND (w.importQuantity - w.exportQuantity) <= 0)
      )
      AND (
          :qrStatus IS NULL
          OR (:qrStatus = 1 AND w.qrCode IS NULL)
          OR (:qrStatus = 2 AND w.qrCode IS NOT NULL)
      )
      AND w.deleted = false
    ORDER BY w.importDate DESC
    """)
  List<Tuple> exportWarehouse(
    @Param("productGroup")    String productGroup,
    @Param("productName")     String productName,
    @Param("origin")          String origin,
    @Param("productDetail")   String productDetail,
    @Param("storageLocation") String storageLocation,
    @Param("importDateFrom")  LocalDate importDateFrom,
    @Param("importDateTo")    LocalDate importDateTo,
    @Param("expiryDateFrom")  LocalDate expiryDateFrom,
    @Param("expiryDateTo")    LocalDate expiryDateTo,
    @Param("status")          Integer status,
    @Param("stockStatus")     Integer stockStatus,
    @Param("qrStatus")        Integer qrStatus,
    @Param("today")           LocalDate today,
    @Param("thresholdDate")   LocalDate thresholdDate
  );


  // Tổng sản phẩm
//  long countByDeletedFalse();
//
//  // Phiếu nhập hôm nay
//  long countByImportDateAndDeletedFalse(LocalDate importDate);
//
//  @Query("SELECT COUNT(w) FROM WarehouseEntity w WHERE w.expiryDate < :today")
//  long countExpired(@Param("today") LocalDate today);

  //lấy cả sản phẩm sắp hết hạn và đã hết hạn, sắp xếp theo ngày hết hạn
  @Query("""
    SELECT w FROM WarehouseEntity w
    WHERE w.expiryDate <= :thresholdDate
    AND (w.importQuantity - w.exportQuantity) > 0
    AND w.deleted = false
    ORDER BY w.expiryDate ASC
    """)
  List<WarehouseEntity> findNearExpiryAndExpiryDate(@Param("thresholdDate") LocalDate thresholdDate);

  // Tổng remainingQuantity theo nhóm
  @Query("""
    SELECT w.productGroup, SUM(w.importQuantity - w.exportQuantity)
    FROM WarehouseEntity w
    WHERE w.deleted = false
    GROUP BY w.productGroup
    """)
  List<Object[]> countByGroup();

  // remainingQuantity sắp hết hạn theo nhóm
  @Query("""
    SELECT w.productGroup, SUM(w.importQuantity - w.exportQuantity)
    FROM WarehouseEntity w
    WHERE w.deleted = false
      AND w.expiryDate >= :today
      AND w.expiryDate <= :thresholdDate
    GROUP BY w.productGroup
    """)
  List<Object[]> countNearExpiryByGroup(@Param("today") LocalDate today,
                                        @Param("thresholdDate") LocalDate thresholdDate);

  @Query("""
    SELECT COALESCE(SUM(w.importQuantity - w.exportQuantity), 0)
    FROM WarehouseEntity w
    WHERE w.expiryDate >= :today
      AND w.expiryDate <= :thresholdDate
      AND w.deleted = false
    """)
  long countNearExpiry(@Param("today") LocalDate today,
                       @Param("thresholdDate") LocalDate thresholdDate);

  // Tổng số lượng còn lại
  @Query("SELECT COALESCE(SUM(w.importQuantity - w.exportQuantity), 0) FROM WarehouseEntity w WHERE w.deleted = false")
  long countByDeletedFalse();

  // Số lượng đã hết hạn
  @Query("SELECT COALESCE(SUM(w.importQuantity - w.exportQuantity), 0) FROM WarehouseEntity w WHERE w.deleted = false AND w.expiryDate < :today")
  long countExpired(@Param("today") LocalDate today);

  // Số lượng sắp hết hạn
  @Query("SELECT COALESCE(SUM(w.importQuantity - w.exportQuantity), 0) FROM WarehouseEntity w WHERE w.deleted = false AND w.expiryDate >= :today AND w.expiryDate <= :thresholdDate")
  long findNearExpiry(@Param("today") LocalDate today, @Param("thresholdDate") LocalDate thresholdDate);

  // Số lượng nhập hôm nay
  @Query("SELECT COALESCE(SUM(w.importQuantity), 0) FROM WarehouseEntity w WHERE w.deleted = false AND w.importDate = :today")
  long countByImportDateAndDeletedFalse(@Param("today") LocalDate today);

}

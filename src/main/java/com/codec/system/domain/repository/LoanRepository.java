package com.codec.system.domain.repository;

import com.codec.system.application.command.response.dashboard.Top5DeviceResponse;
import com.codec.system.domain.entity.LoanEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, String> {

  boolean existsByLoanCode(String loanCode);

  @Query(value = """
    SELECT l.id                 AS "id",
           l.loan_code          AS "loanCode",
           l.borrower_id        AS "borrowerId",
           u.full_name          AS "borrowerName",
           l.device_id          AS "deviceId",
           d.device_code        AS "itemCode",
           d.name               AS "itemName",
           l.quantity           AS "quantity",
           l.borrow_date        AS "borrowDate",
           l.borrow_period      AS "borrowPeriod",
           l.return_period      AS "returnPeriod",
           l.actual_return_date AS "actualReturnDate",
           l.status             AS "status",
           l.note               AS "note",
           l.created_date       AS "createdDate",
           l.modified_date      AS "modifiedDate"
    FROM loans l
    LEFT JOIN users u   ON u.id = l.borrower_id
    LEFT JOIN devices d ON d.id = l.device_id
    WHERE l.deleted = false
      AND (CAST(:loanCode AS TEXT) IS NULL OR CAST(:loanCode AS TEXT) = ''
           OR LOWER(l.loan_code) LIKE LOWER(CONCAT('%', CAST(:loanCode AS TEXT), '%')))
      AND (CAST(:borrowerName AS TEXT) IS NULL OR CAST(:borrowerName AS TEXT) = ''
           OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', CAST(:borrowerName AS TEXT), '%')))
      AND (CAST(:status AS INTEGER) IS NULL OR l.status = CAST(:status AS INTEGER))
      AND (CAST(:fromDate AS TIMESTAMP) IS NULL OR l.borrow_date >= CAST(:fromDate AS TIMESTAMP))
      AND (CAST(:toDate AS TIMESTAMP) IS NULL OR l.borrow_date <= CAST(:toDate AS TIMESTAMP))
    ORDER BY l.created_date DESC
    """, nativeQuery = true)
  Page<Tuple> getAllLoan(@Param("loanCode") String loanCode,
                         @Param("borrowerName") String borrowerName,
                         @Param("status") Integer status,
                         @Param("fromDate") Date fromDate,
                         @Param("toDate") Date toDate,
                         Pageable pageable);

  @Query(value = """
    SELECT l.id                 AS "id",
           l.loan_code          AS "loanCode",
           l.borrower_id        AS "borrowerId",
           u.full_name          AS "borrowerName",
           l.device_id          AS "deviceId",
           d.device_code        AS "itemCode",
           d.name               AS "itemName",
           l.quantity           AS "quantity",
           l.borrow_date        AS "borrowDate",
           l.borrow_period      AS "borrowPeriod",
           l.return_period      AS "returnPeriod",
           l.actual_return_date AS "actualReturnDate",
           l.status             AS "status",
           l.note               AS "note",
           l.created_date       AS "createdDate",
           l.modified_date      AS "modifiedDate"
    FROM loans l
    LEFT JOIN users u   ON u.id = l.borrower_id
    LEFT JOIN devices d ON d.id = l.device_id
    WHERE l.deleted = false
      AND (CAST(:loanCode AS TEXT) IS NULL OR CAST(:loanCode AS TEXT) = ''
           OR LOWER(l.loan_code) LIKE LOWER(CONCAT('%', CAST(:loanCode AS TEXT), '%')))
      AND (CAST(:status AS INTEGER) IS NULL OR l.status = CAST(:status AS INTEGER))
      AND (CAST(:fromDate AS TIMESTAMP) IS NULL OR l.borrow_date >= CAST(:fromDate AS TIMESTAMP))
      AND (CAST(:toDate AS TIMESTAMP) IS NULL OR l.borrow_date <= CAST(:toDate AS TIMESTAMP))
      AND l.borrower_id = :userId
    ORDER BY l.created_date DESC
    """, nativeQuery = true)
  Page<Tuple> getAllLoanForUser(@Param("loanCode") String loanCode,
                                @Param("status") Integer status,
                                @Param("fromDate") Date fromDate,
                                @Param("toDate") Date toDate,
                                Pageable pageable,
                                @Param("userId") String userId);

  /**
   * Lượt mượn: đếm tất cả phiếu mượn trong khoảng thời gian,
   * bao gồm mọi trạng thái (đang mượn/đã trả/trả chậm/mất).
   * status: 1-đang mượn, 2-đã trả, 3-trả chậm, 4-mất thiết bị
   */
  @Query("""
            SELECT COUNT(l)
            FROM LoanEntity l
            WHERE l.deleted = false
              AND l.borrowDate >= :fromDate
              AND l.borrowDate <= :toDate
            """)
  Long countLoansInRange(
    @Param("fromDate") Date fromDate,
    @Param("toDate") Date toDate
  );

  /**
   * Lượt mất: chỉ đếm phiếu có status = 4 (Mất thiết bị).
   */
  @Query("""
            SELECT COUNT(l)
            FROM LoanEntity l
            WHERE l.deleted = false
              AND l.status = 4
              AND l.borrowDate >= :fromDate
              AND l.borrowDate <= :toDate
            """)
  Long countLostInRange(
    @Param("fromDate") Date fromDate,
    @Param("toDate") Date toDate
  );

  /**
   * Top 5 thiết bị được mượn nhiều nhất trong khoảng thời gian.
   * Join DeviceEntity để lấy tên, mã thiết bị.
   * Group by device → ORDER BY COUNT DESC → LIMIT 5 (JPA dùng setMaxResults).
   */
  /**
   * Top 5 thiết bị mượn nhiều nhất — native SQL để dùng LIMIT trực tiếp.
   * Dùng interface projection (Top5DeviceResponse) map theo tên cột AS.
   */
  @Query(value = """
            SELECT d.id          AS deviceId,
                   d.device_code AS deviceCode,
                   d.name        AS deviceName,
                   COUNT(l.id)   AS totalLoans
            FROM loans l
            JOIN devices d ON d.id = l.device_id
            WHERE l.deleted = false
              AND d.deleted = false
              AND l.device_id IS NOT NULL
              AND l.borrow_date >= :fromDate
              AND l.borrow_date <= :toDate
            GROUP BY d.id, d.device_code, d.name
            ORDER BY totalLoans DESC
            LIMIT 5
            """, nativeQuery = true)
  List<Tuple> findTop5BorrowedDevices(
    @Param("fromDate") Date fromDate,
    @Param("toDate") Date toDate
  );

  /**
   * Thống kê phiếu mượn theo trạng thái — trả về Tuple để map vào DTO.
   * Dùng JPQL SUM+CASE, alias khớp với tên field trong LoanStatusStatsResponse.
   */
  @Query("""
            SELECT
                SUM(CASE WHEN l.status = 1 THEN 1L ELSE 0L END) AS borrowing,
                SUM(CASE WHEN l.status = 2 THEN 1L ELSE 0L END) AS returned,
                SUM(CASE WHEN l.status = 3 THEN 1L ELSE 0L END) AS lateReturn,
                SUM(CASE WHEN l.status = 4 THEN 1L ELSE 0L END) AS lost
            FROM LoanEntity l
            WHERE l.deleted = false
              AND l.borrowDate >= :fromDate
              AND l.borrowDate <= :toDate
            """)
  Tuple countLoansByStatus(
    @Param("fromDate") Date fromDate,
    @Param("toDate") Date toDate
  );
}

package com.jarviswuod.improvedbillingsystem.payment;

import com.jarviswuod.improvedbillingsystem.dashboard.BillingSummaryDto;
import com.jarviswuod.improvedbillingsystem.dashboard.CustomersDto;
import com.jarviswuod.improvedbillingsystem.dashboard.MonthlyRevenueDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByTransactionNumber(String number);

    @Query(value = """
            SELECT 
            COUNT(DISTINCT c.id) AS totalCustomers,
            COUNT(DISTINCT i.id) AS totalInvoices,
            COALESCE(SUM(i.amount),0) AS totalAmountInvoiced,
            COALESCE(SUM(p.amount),0) AS totalAmountPaid,
            COALESCE(SUM(i.amount),0) - COALESCE(SUM(p.amount),0) AS outstandingBalance
            FROM customers c
            LEFT JOIN invoices i 
                ON i.customer_id = c.id
                AND i.created_at BETWEEN :startDate AND :endDate
            LEFT JOIN payments p 
                ON p.invoice_id = i.id
                AND p.payment_date BETWEEN :startDate AND :endDate
            WHERE c.created_at BETWEEN :startDate AND :endDate
            """, nativeQuery = true)
    BillingSummaryDto getSummary(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query(value = """
            SELECT 
                (SELECT COUNT(*) FROM customers) as totalCustomers,
                (SELECT COUNT(*) FROM invoices) as totalInvoices,
                (SELECT COALESCE(SUM(amount), 0) FROM invoices) as totalAmountInvoiced,
                (SELECT COALESCE(SUM(amount), 0) FROM payments) as totalAmountPaid,
                ((SELECT COALESCE(SUM(amount), 0) FROM invoices) - 
                 (SELECT COALESCE(SUM(amount), 0) FROM payments)) as outstandingBalance
            """, nativeQuery = true)
    BillingSummaryDto getSummary__(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query(value = """
            SELECT 
                (SELECT COUNT(*) FROM customers WHERE created_at BETWEEN :start AND :end) as totalCustomers,
                (SELECT COUNT(*) FROM invoices WHERE created_at BETWEEN :start AND :end) as totalInvoices,
                (SELECT COALESCE(SUM(amount), 0) FROM invoices WHERE created_at BETWEEN :start AND :end) as totalAmountInvoiced,
                (SELECT COALESCE(SUM(amount), 0) FROM payments WHERE payment_date BETWEEN :startDate AND :endDate) as totalAmountPaid,
                ((SELECT COALESCE(SUM(amount), 0) FROM invoices WHERE created_at BETWEEN :start AND :end) - 
                    (SELECT COALESCE(SUM(amount), 0) FROM payments WHERE payment_date BETWEEN :startDate AND :endDate)) as outstandingBalance
            """, nativeQuery = true)
    BillingSummaryDto getSummary__1(
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query(value = """
            WITH stats AS (
                SELECT 
                    (SELECT COUNT(*) FROM customers WHERE created_at BETWEEN :start AND :end) as tc,
                    (SELECT COUNT(*) FROM invoices WHERE created_at BETWEEN :start AND :end) as ti,
                    (SELECT COALESCE(SUM(amount), 0) FROM invoices WHERE created_at BETWEEN :start AND :end) as tai,
                    (SELECT COALESCE(SUM(amount), 0) FROM payments WHERE payment_date BETWEEN :startDate AND :endDate) as tap
            )
            SELECT 
                tc as totalCustomers, 
                ti as totalInvoices, 
                tai as totalAmountInvoiced, 
                tap as totalAmountPaid,
                (tai - tap) as outstandingBalance
            FROM stats
            """, nativeQuery = true)
    BillingSummaryDto getSummary__2(
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query(value = """
    WITH stats AS (
        SELECT 
            (SELECT COUNT(*) FROM customers 
             WHERE (:start IS NULL OR created_at >= :start) 
               AND (:end IS NULL OR created_at <= :end)
               AND is_deleted = false) as tc,
               
            (SELECT COUNT(*) FROM invoices 
             WHERE (:start IS NULL OR created_at >= :start) 
               AND (:end IS NULL OR created_at <= :end)
               AND is_deleted = false) as ti,
               
            (SELECT COALESCE(SUM(amount), 0) FROM invoices 
             WHERE (:start IS NULL OR created_at >= :start) 
               AND (:end IS NULL OR created_at <= :end)
               AND is_deleted = false) as tai,
               
            (SELECT COALESCE(SUM(amount), 0) FROM payments 
             WHERE (:pStart IS NULL OR payment_date >= :pStart) 
               AND (:pEnd IS NULL OR payment_date <= :pEnd)
               AND is_deleted = false) as tap
    )
    SELECT 
        tc as totalCustomers, 
        ti as totalInvoices, 
        tai as totalAmountInvoiced, 
        tap as totalAmountPaid,
        (tai - tap) as outstandingBalance
    FROM stats
    """, nativeQuery = true)
    BillingSummaryDto getSummary__3(
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("pStart") LocalDate pStart,
            @Param("pEnd") LocalDate pEnd
    );


    @Query(value = """
            WITH stats AS (
                SELECT 
                    (SELECT COUNT(*) FROM customers 
                     WHERE (CAST(:start as date) is NULL OR CAST(:start as date) <= created_at)
                       AND (CAST(:end as date) is NULL OR CAST(:end as date) >= created_at)
                       AND is_deleted = false) as tc,
            
                    (SELECT COUNT(*) FROM invoices 
                     WHERE (CAST(:start as date) is NULL OR CAST(:start as date) <= created_at)
                       AND (CAST(:end as date) is NULL OR CAST(:end as date) >= created_at)
                       AND is_deleted = false) as ti,
            
                    (SELECT COALESCE(SUM(amount), 0) FROM invoices 
                     WHERE (CAST(:start as date) is NULL OR CAST(:start as date) <= created_at)
                       AND (CAST(:end as date) is NULL OR CAST(:end as date) >= created_at)
                       AND is_deleted = false) as tai,
            
                    (SELECT COALESCE(SUM(amount), 0) FROM payments 
                     WHERE (CAST(:pStart as date) is NULL OR CAST(:pStart as date) <= payment_date)
                       AND (CAST(:pEnd as date) is NULL OR CAST(:pEnd as date) >= payment_date)
                       AND is_deleted = false) as tap
            )
            SELECT 
                tc as totalCustomers, 
                ti as totalInvoices, 
                tai as totalAmountInvoiced, 
                tap as totalAmountPaid,
                (tai - tap) as outstandingBalance
            FROM stats
            """, nativeQuery = true)
    BillingSummaryDto getSummary__4(
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("pStart") LocalDate pStart,
            @Param("pEnd") LocalDate pEnd
    );

    @Query(value = """
            SELECT
            COUNT(DISTINCT c.id) AS totalCustomers,
            COUNT(DISTINCT i.id) AS totalInvoices,
            COALESCE(SUM(i.invoice_amount),0) AS totalAmountInvoiced,
            COALESCE(SUM(p.total_paid),0) AS totalAmountPaid,
            COALESCE(SUM(i.invoice_amount),0) - COALESCE(SUM(p.total_paid),0) AS outstandingBalance
            FROM customers c
            
            LEFT JOIN (
                SELECT id, customer_id, amount AS invoice_amount
                FROM invoices
                WHERE is_deleted = false
                AND created_at BETWEEN :startDate AND :endDate
            ) i ON i.customer_id = c.id
            
            LEFT JOIN (
                SELECT invoice_id, SUM(amount) AS total_paid
                FROM payments
                WHERE is_deleted = false
                AND payment_date BETWEEN :startDate AND :endDate
                GROUP BY invoice_id
            ) p ON p.invoice_id = i.id
            
            WHERE c.is_deleted = false
            AND c.created_at BETWEEN :startDate AND :endDate;
            """, nativeQuery = true)
    BillingSummaryDto getSummary_(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

//    ------------------------------------------------------------------------------------
//    ------------------------------------------------------------------------------------
//    ------------------------------------------------------------------------------------
//    ------------------------------------------------------------------------------------
//    ------------------------------------------------------------------------------------

    @Query(value = """
            SELECT c.name AS customerName, SUM(p.amount) AS totalPaid
            FROM   payments p
            JOIN   invoices  i ON i.id = p.invoice_id
            JOIN   customers c ON c.id = i.customer_id
            WHERE  (CAST(:startDate AS date) IS NULL OR p.payment_date >= CAST(:startDate AS date))
              AND  (CAST(:endDate   AS date) IS NULL OR p.payment_date <= CAST(:endDate   AS date))
            GROUP  BY c.id, c.name
            ORDER  BY totalPaid DESC
            LIMIT  :limit
            """, nativeQuery = true)
    List<CustomersDto> findTopCustomers(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("limit") int limit
    );


    @Query(value = """
                SELECT TO_CHAR(payment_date, 'YYYY-MM') as month, SUM(amount) as total
                FROM payments
                WHERE payment_date BETWEEN :startDate AND :endDate
                GROUP BY month
                ORDER BY month DESC
            """, nativeQuery = true)
    List<MonthlyRevenueDto> getMonthlyRevenue(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}

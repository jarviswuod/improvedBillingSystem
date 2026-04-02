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
    BillingSummaryDto getSummary(
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("pStart") LocalDate pStart,
            @Param("pEnd") LocalDate pEnd
    );


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

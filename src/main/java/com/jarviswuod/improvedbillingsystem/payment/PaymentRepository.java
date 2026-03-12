package com.jarviswuod.improvedbillingsystem.payment;

import com.jarviswuod.improvedbillingsystem.dashboard.BillingSummaryDto;
import com.jarviswuod.improvedbillingsystem.dashboard.CustomersDto;
import com.jarviswuod.improvedbillingsystem.dashboard.MonthlyRevenueDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
                FROM payments p
                JOIN invoices i ON p.invoice_id = i.id
                JOIN customers c ON i.customer_id = c.id;
            """, nativeQuery = true)
    BillingSummaryDto getSummary();

    /*

        @Query(value = """
                SELECT
                COUNT(DISTINCT c.id),
                COUNT(DISTINCT i.id),
                COALESCE(SUM(i.amount),0),
                COALESCE(SUM(p.amount),0),
                COALESCE(SUM(i.amount),0) - COALESCE(SUM(p.amount),0)
                FROM payments p
                JOIN invoices i ON p.invoice_id = i.id
                JOIN customers c ON i.customer_id = c.id
                WHERE  (CAST(:startDate AS date) IS NULL OR p.payment_date >= CAST(:startDate AS date))
                  AND  (CAST(:endDate   AS date) IS NULL OR p.payment_date <= CAST(:endDate   AS date))

                """, nativeQuery = true)
        BillingSummaryDto getSummaryRange(
                @Param("startDate") LocalDate startDate,
                @Param("endDate") LocalDate endDate
        );

     */

    @Query(value = """
            SELECT 
             COUNT(DISTINCT c.id) AS totalCustomers,
             COUNT(DISTINCT i.id) AS totalInvoices,
             COALESCE(SUM(i.amount),0) AS totalAmountInvoiced,
             COALESCE(SUM(p.amount),0) AS totalAmountPaid,
             COALESCE(SUM(i.amount),0) - COALESCE(SUM(p.amount),0) AS outstandingBalance
            FROM customers c
            LEFT JOIN invoices i ON i.customer_id = c.id
            LEFT JOIN payments p ON p.invoice_id = i.id
              AND (CAST(:startDate AS date) IS NULL OR p.payment_date >= CAST(:startDate AS date))
              AND (CAST(:endDate   AS date) IS NULL OR p.payment_date <= CAST(:endDate   AS date))

            """, nativeQuery = true)
    BillingSummaryDto getSummaryRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
/*

    @Query(value = """
            SELECT COUNT(c) as totalCustomers, COUNT(i) as totalInvoices,
                    SUM(i.amount) as totalAmountInvoiced, SUM(p.amount) as totalAmountPaid,
                    SUM(i.amount-p.amount) as outstandingBalance
            FROM invoices i
            JOIN customers c ON c.id = i.customer_id
            JOIN payments p ON p.invoice_id = i.id
            WHERE p.payment_date BETWEEN startDate AND endDate
            """, nativeQuery = true)
    BillingSummaryDto invoicesSummary();


        @Query(value = """
                SELECT COUNT(c) as totalCustomers, COUNT(i) as totalInvoices,
                        SUM(i.amount) as totalAmountInvoiced, SUM(p.amount) as totalAmountPaid,
                        SUM(i.amount - p.amount) as outstandingBalance
                FROM invoices i
                JOIN customers c ON c.id = i.customer_id
                JOIN payments p ON p.invoice_id = i.id
                WHERE p.payment_date BETWEEN :startDate AND :endDate
                """, nativeQuery = true)
        DashboardSummaryDto invoicesSummary(
                @Param("startDate") LocalDate startDate,
                @Param("endDate") LocalDate endDate
        );

    @Query(value = """
            SELECT COUNT(DISTINCT (c.id)) as totalCustomers, COUNT(DISTINCT (i.id)) as totalInvoices,
                    SUM(i.amount) as totalAmountInvoiced, SUM(p.amount) as totalAmountPaid,
                    SUM(i.amount - p.amount) as outstandingBalance
            FROM payments p
            JOIN invoices i ON i.id = p.invoice_id
            JOIN customers c ON c.id = i.customer_id
            WHERE  (CAST(:startDate AS date) IS NULL OR p.payment_date >= CAST(:startDate AS date))
              AND  (CAST(:endDate   AS date) IS NULL OR p.payment_date <= CAST(:endDate   AS date))
            """, nativeQuery = true)
    BillingSummaryDto invoicesSummary(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

 */


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
    List<CustomersDto> findTopCustomers(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("limit") int limit);


    @Query(value = """
                SELECT TO_CHAR(payment_date, 'YYYY-MM') as month, SUM(amount) as total
                FROM payments
                WHERE payment_date BETWEEN :startDate AND :endDate
                GROUP BY month
                ORDER BY month DESC
            """, nativeQuery = true)
    List<MonthlyRevenueDto> getMonthlyRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}

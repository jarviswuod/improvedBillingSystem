package com.jarviswuod.improvedbillingsystem.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query(value = """
            SELECT 
            COALESCE(i.id, 0) AS invoiceNumber,
            c.name AS customerName,
            COALESCE(i.amount, 0) AS amount,
            COALESCE(p.amount, 0) AS amountPaid,
            (COALESCE(i.amount, 0) - COALESCE(p.amount, 0)) AS balance,
            i.due_data AS dueDate,
            COALESCE((CURRENT_DATE - i.due_data), 0) AS daysOverdue,
            i.status AS status 
            FROM customers c 
            LEFT JOIN invoices i 
                ON i.customer_id = c.id 
                AND i.creation_date BETWEEN :startDate AND :endDate
            LEFT JOIN payments p
                ON p.invoice_id = i.id
            WHERE c.id =:customerId
            """, nativeQuery = true)
    List<OverdueInvoiceDto> overDueInvoices(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}

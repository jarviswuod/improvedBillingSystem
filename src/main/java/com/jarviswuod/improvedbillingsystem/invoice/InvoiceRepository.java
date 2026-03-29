package com.jarviswuod.improvedbillingsystem.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {


    @Query("""
        SELECT i
        FROM Invoice i
        LEFT JOIN i.payments p
        WHERE i.dueDate < :today
          AND (:customerId IS NULL OR i.customer.id = :customerId)
          AND i.createdAt >= COALESCE(:startCreatedAt, i.createdAt)
          AND i.createdAt <= COALESCE(:endCreatedAt, i.createdAt)
        GROUP BY i
        HAVING COALESCE(SUM(p.amount), 0) < i.amount
    """)
    List<Invoice> findOverdueInvoices(
            @Param("customerId") Long customerId,
            @Param("today") LocalDate today,
            @Param("startCreatedAt") Instant startCreatedAt,
            @Param("endCreatedAt") Instant endCreatedAt
    );
}

package com.jarviswuod.improvedbillingsystem.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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


    /*

    Option 1
        Fetch all invoices
        Fetch all their payments
        calculate payments in java
        Update the status based on sum payments

        Invoices with payments at once
        Invoices first them payments later

     */
    @Query("""
        SELECT i
        FROM Invoice i
        LEFT JOIN FETCH i.payments p
    """)
    List<Invoice> fetchAllInvoicesWithAllPayments(

    );

    /*

        Option 2
            Fetch individual invoice with all it's payments
            Sum in up in java
            Update the status
            Loop for all invoices available

     */
    @Query("""
        SELECT i
        FROM Invoice i 
        LEFT JOIN i.payments p
        WHERE i.id = :id
    """)
    Optional<Invoice> fetchAnInvoicesWithAllPayments(
            @Param("id") Long id
    );

    /*

        Option 3
            Fetch invoice with aggregate sum
            Update Invoice status based on result
            Loop for all invoices

     */
    @Query("""
        SELECT i, COALESCE(SUM(p.amount), 0)
        FROM Invoice i 
        LEFT JOIN i.payments p
        WHERE i.id = :id
        GROUP BY i
    """)
    Optional<Object>  fetchSingleInvoicesWithSum(
            @Param("id") Long id
    );


    /*

        Option 4
            Fetch all invoices with corresponding aggregate SUM
            Loop in java code updating every individual invoice status

     */
    @Query("""
        SELECT i,
        COALESCE(SUM(p.amount), 0)
        FROM Invoice i
        LEFT JOIN i.payments p
            GROUP BY i
    """)
    List<Object> fetchAllInvoicesWithSum(

    );
}

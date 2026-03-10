package com.jarviswuod.improvedbillingsystem.invoice;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jarviswuod.improvedbillingsystem.customer.Customer;
import com.jarviswuod.improvedbillingsystem.payment.Payment;
import com.jarviswuod.improvedbillingsystem.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invoices")
@Builder
public class Invoice extends BaseEntity {

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate dueData;

    private InvoiceStatus status;

    private Double balance;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "invoice")
    @JsonBackReference
    private List<Payment> payments;
}

package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.utils.BaseEntity;
import com.jarviswuod.improvedbillingsystem.customer.Customer;
import com.jarviswuod.improvedbillingsystem.payment.Payment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private LocalDateTime dueData;

    private InvoiceStatus status;

    private Double balance;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "invoice")
    private List<Payment> payments;
}

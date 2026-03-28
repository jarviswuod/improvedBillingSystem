package com.jarviswuod.improvedbillingsystem.payment;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jarviswuod.improvedbillingsystem.invoice.Invoice;
import com.jarviswuod.improvedbillingsystem.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payments")
@Builder
public class Payment extends BaseEntity {

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate paymentDate;

    private PaymentMethod paymentMethod;

    @Column(nullable = false, unique = true)
    private String transactionNumber;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    @JsonManagedReference
    private Invoice invoice;
}

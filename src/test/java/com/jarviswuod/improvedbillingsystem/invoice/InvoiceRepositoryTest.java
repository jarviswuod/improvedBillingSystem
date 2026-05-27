package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.AbstractTestContainerTest;
import com.jarviswuod.improvedbillingsystem.customer.Customer;
import com.jarviswuod.improvedbillingsystem.customer.CustomerRepository;
import com.jarviswuod.improvedbillingsystem.payment.Payment;
import com.jarviswuod.improvedbillingsystem.payment.PaymentMethod;
import com.jarviswuod.improvedbillingsystem.payment.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class InvoiceRepositoryTest extends AbstractTestContainerTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PaymentRepository paymentRepository;


    @Test
    void findOverdueInvoicesShouldReturnInvoicesWithOutstandingBalance() {
        // Given
        Customer customer = customerRepository.saveAndFlush(customer("Jarvis", "overdue@example.com"));
        Invoice overdueInvoice = invoiceRepository.saveAndFlush(invoice(
                customer,
                BigDecimal.valueOf(1000),
                LocalDate.now().minusDays(10),
                InvoiceStatus.PARTIALLY_PAID
        ));
        paymentRepository.saveAndFlush(payment(overdueInvoice, BigDecimal.valueOf(400), "OVERDUE-TXN-1"));

        Invoice fullyPaidInvoice = invoiceRepository.saveAndFlush(invoice(
                customer,
                BigDecimal.valueOf(1000),
                LocalDate.now().minusDays(5),
                InvoiceStatus.PAID
        ));
        paymentRepository.saveAndFlush(payment(fullyPaidInvoice, BigDecimal.valueOf(1000), "PAID-TXN-1"));

        Invoice futureInvoice = invoiceRepository.saveAndFlush(invoice(
                customer,
                BigDecimal.valueOf(1000),
                LocalDate.now().plusDays(5),
                InvoiceStatus.PENDING
        ));

        // When
        List<OverdueInvoiceDto> overdueInvoices = invoiceRepository.findOverdueInvoices(
                customer.getId(),
                LocalDate.now(),
                null,
                null
        );

        // Then
        assertThat(overdueInvoices)
                .extracting(OverdueInvoiceDto::invoiceNumber)
                .contains(overdueInvoice.getId())
                .doesNotContain(fullyPaidInvoice.getId(), futureInvoice.getId());
        assertThat(overdueInvoices.get(0).balance()).isEqualByComparingTo(BigDecimal.valueOf(600));
    }


    private Customer customer(String name, String email) {
        Customer customer = Customer.builder()
                .name(name)
                .email(email)
                .phone("+254712345678")
                .build();
        customer.setCreatedAt(Instant.now());
        customer.setUpdatedAt(Instant.now());
        return customer;
    }


    private Invoice invoice(Customer customer, BigDecimal amount, LocalDate dueDate, InvoiceStatus status) {
        Invoice invoice = Invoice.builder()
                .amount(amount)
                .dueDate(dueDate)
                .status(status)
                .customer(customer)
                .build();
        invoice.setCreatedAt(Instant.now());
        invoice.setUpdatedAt(Instant.now());
        return invoice;
    }


    private Payment payment(Invoice invoice, BigDecimal amount, String transactionNumber) {
        Payment payment = Payment.builder()
                .amount(amount)
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.MPESA)
                .transactionNumber(transactionNumber)
                .invoice(invoice)
                .build();
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(Instant.now());
        return payment;
    }
}

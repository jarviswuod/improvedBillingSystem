package com.jarviswuod.improvedbillingsystem.payment;

import com.jarviswuod.improvedbillingsystem.AbstractTestContainerTest;
import com.jarviswuod.improvedbillingsystem.customer.Customer;
import com.jarviswuod.improvedbillingsystem.customer.CustomerRepository;
import com.jarviswuod.improvedbillingsystem.dashboard.BillingSummaryDto;
import com.jarviswuod.improvedbillingsystem.dashboard.CustomersDto;
import com.jarviswuod.improvedbillingsystem.dashboard.MonthlyRevenueDto;
import com.jarviswuod.improvedbillingsystem.invoice.Invoice;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceRepository;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceStatus;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class PaymentRepositoryTest extends AbstractTestContainerTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;


    @Test
    void findByTransactionNumberShouldReturnPayment() {
        // Given
        Invoice invoice = savedInvoice("transaction@example.com", BigDecimal.valueOf(1000));
        Payment payment = paymentRepository.saveAndFlush(payment(invoice, BigDecimal.valueOf(400), "TXN-LOOKUP"));

        // When
        Payment response = paymentRepository.findByTransactionNumber(payment.getTransactionNumber());

        // Then
        assertNotNull(response);
        assertEquals(payment.getId(), response.getId());
    }


    @Test
    void getSummaryShouldReturnBillingTotals() {
        // Given
        Invoice invoice = savedInvoice("summary@example.com", BigDecimal.valueOf(1000));
        paymentRepository.saveAndFlush(payment(invoice, BigDecimal.valueOf(400), "TXN-SUMMARY"));

        // When
        BillingSummaryDto summary = paymentRepository.getSummary(null, null, null, null);

        // Then
        assertThat(summary.totalCustomers()).isGreaterThanOrEqualTo(1);
        assertThat(summary.totalInvoices()).isGreaterThanOrEqualTo(1);
        assertThat(summary.totalAmountInvoiced()).isGreaterThanOrEqualTo(BigDecimal.valueOf(1000));
        assertThat(summary.totalAmountPaid()).isGreaterThanOrEqualTo(BigDecimal.valueOf(400));
    }


    @Test
    void findTopCustomersShouldOrderCustomersByTotalPaid() {
        // Given
        Invoice firstInvoice = savedInvoice("top-one@example.com", BigDecimal.valueOf(2000));
        paymentRepository.saveAndFlush(payment(firstInvoice, BigDecimal.valueOf(1500), "TXN-TOP-1"));

        Invoice secondInvoice = savedInvoice("top-two@example.com", BigDecimal.valueOf(2000));
        paymentRepository.saveAndFlush(payment(secondInvoice, BigDecimal.valueOf(500), "TXN-TOP-2"));

        // When
        List<CustomersDto> topCustomers = paymentRepository.findTopCustomers(
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1),
                2
        );

        // Then
        assertThat(topCustomers).hasSizeGreaterThanOrEqualTo(2);
        assertThat(topCustomers.get(0).totalPaid()).isGreaterThanOrEqualTo(topCustomers.get(1).totalPaid());
    }


    @Test
    void getMonthlyRevenueShouldGroupPaymentsByMonth() {
        // Given
        Invoice invoice = savedInvoice("monthly@example.com", BigDecimal.valueOf(1000));
        paymentRepository.saveAndFlush(payment(invoice, BigDecimal.valueOf(400), "TXN-MONTHLY"));

        // When
        List<MonthlyRevenueDto> revenue = paymentRepository.getMonthlyRevenue(
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusDays(1)
        );

        // Then
        assertThat(revenue).isNotEmpty();
        assertThat(revenue.get(0).total()).isGreaterThanOrEqualTo(BigDecimal.valueOf(400));
    }


    private Invoice savedInvoice(String customerEmail, BigDecimal amount) {
        Customer customer = Customer.builder()
                .name(customerEmail)
                .email(customerEmail)
                .phone("+254712345678")
                .build();
        customer.setCreatedAt(Instant.now());
        customer.setUpdatedAt(Instant.now());
        Customer savedCustomer = customerRepository.saveAndFlush(customer);

        Invoice invoice = Invoice.builder()
                .amount(amount)
                .dueDate(LocalDate.now().plusDays(30))
                .status(InvoiceStatus.PENDING)
                .customer(savedCustomer)
                .build();
        invoice.setCreatedAt(Instant.now());
        invoice.setUpdatedAt(Instant.now());
        return invoiceRepository.saveAndFlush(invoice);
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

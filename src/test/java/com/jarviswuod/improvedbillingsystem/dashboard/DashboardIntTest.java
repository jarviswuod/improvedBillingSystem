package com.jarviswuod.improvedbillingsystem.dashboard;

import com.jarviswuod.improvedbillingsystem.AbstractTestContainerTest;
import com.jarviswuod.improvedbillingsystem.customer.CustomerDto;
import com.jarviswuod.improvedbillingsystem.customer.CustomerResponseDto;
import com.jarviswuod.improvedbillingsystem.invoice.Invoice;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceDto;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceRepository;
import com.jarviswuod.improvedbillingsystem.payment.PaymentDto;
import com.jarviswuod.improvedbillingsystem.payment.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class DashboardIntTest extends AbstractTestContainerTest {

    private static final String CUSTOMER_API_PATH = "/api/customers";
    private static final String INVOICE_API_PATH = "/api/invoices";
    private static final String PAYMENT_API_PATH = "/api/payments";
    private static final String DASHBOARD_API_PATH = "/api/dashboard";

    @Autowired
    private org.springframework.boot.test.web.client.TestRestTemplate restTemplate;

    @Autowired
    private InvoiceRepository invoiceRepository;


    private Long createCustomer() {
        CustomerDto request = new CustomerDto(
                "Dashboard Test User",
                "dashboard-user-" + UUID.randomUUID() + "@gmail.com",
                "+254712345678"
        );

        ResponseEntity<CustomerResponseDto> create = restTemplate.exchange(
                CUSTOMER_API_PATH, POST, new HttpEntity<>(request), CustomerResponseDto.class);

        assertEquals(HttpStatus.CREATED, create.getStatusCode());
        assertThat(create.getBody()).isNotNull();

        return Objects.requireNonNull(create.getBody()).id();
    }


    private Long createInvoice(BigDecimal amount) {
        Long customerId = createCustomer();
        InvoiceDto request = new InvoiceDto(amount, LocalDate.now().plusDays(30), customerId);

        ResponseEntity<String> create = restTemplate.exchange(
                INVOICE_API_PATH, POST, new HttpEntity<>(request), String.class);

        assertEquals(HttpStatus.CREATED, create.getStatusCode());

        return invoiceRepository.findAll()
                .stream()
                .filter(invoice -> invoice.getCustomer().getId().equals(customerId))
                .filter(invoice -> invoice.getAmount().compareTo(amount) == 0)
                .map(Invoice::getId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Invoice not found after creation"));
    }


    private void createPayment(Long invoiceId, BigDecimal amount) {
        PaymentDto request = new PaymentDto(
                invoiceId,
                amount,
                LocalDate.now(),
                PaymentMethod.MPESA,
                "TXN-" + UUID.randomUUID()
        );

        ResponseEntity<String> create = restTemplate.exchange(
                PAYMENT_API_PATH, POST, new HttpEntity<>(request), String.class);

        assertEquals(HttpStatus.CREATED, create.getStatusCode());
    }


    @Test
    void shouldReturnBillingSummary() {
        // Given
        Long invoiceId = createInvoice(BigDecimal.valueOf(1000));
        createPayment(invoiceId, BigDecimal.valueOf(400));

        // When
        ResponseEntity<BillingSummaryDto> response = restTemplate.exchange(
                DASHBOARD_API_PATH + "/summary",
                GET,
                null,
                BillingSummaryDto.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertThat(response.getBody().totalCustomers()).isGreaterThanOrEqualTo(1);
        assertThat(response.getBody().totalInvoices()).isGreaterThanOrEqualTo(1);
    }


    @Test
    void shouldReturnTopCustomers() {
        // Given
        Long invoiceId = createInvoice(BigDecimal.valueOf(1000));
        createPayment(invoiceId, BigDecimal.valueOf(400));

        // When
        ResponseEntity<List<CustomersDto>> response = restTemplate.exchange(
                DASHBOARD_API_PATH + "/top-customers?startDate=" + LocalDate.now().minusDays(1)
                        + "&endDate=" + LocalDate.now() + "&limit=5",
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull().isNotEmpty();
    }


    @Test
    void shouldReturnMonthlyRevenue() {
        // Given
        Long invoiceId = createInvoice(BigDecimal.valueOf(1000));
        createPayment(invoiceId, BigDecimal.valueOf(400));

        // When
        ResponseEntity<List<MonthlyRevenueDto>> response = restTemplate.exchange(
                DASHBOARD_API_PATH + "/monthly-revenue?startDate=" + LocalDate.now().minusMonths(1)
                        + "&endDate=" + LocalDate.now(),
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull().isNotEmpty();
    }


    @Test
    void shouldReturn409WhenSummaryDateRangeIsInvalid() {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                DASHBOARD_API_PATH + "/summary?startDate=" + LocalDate.now()
                        + "&endDate=" + LocalDate.now().minusDays(1),
                GET,
                null,
                String.class
        );

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}

package com.jarviswuod.improvedbillingsystem.payment;

import com.jarviswuod.improvedbillingsystem.AbstractTestContainerTest;
import com.jarviswuod.improvedbillingsystem.customer.CustomerDto;
import com.jarviswuod.improvedbillingsystem.customer.CustomerResponseDto;
import com.jarviswuod.improvedbillingsystem.invoice.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
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
import static org.springframework.http.HttpMethod.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class PaymentIntTest extends AbstractTestContainerTest {

    private static final String CUSTOMER_API_PATH = "/api/customers";
    private static final String INVOICE_API_PATH = "/api/invoices";
    private static final String PAYMENT_API_PATH = "/api/payments";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;


    private Long createCustomer() {
        CustomerDto request = new CustomerDto(
                "Payment Test User",
                "payment-user-" + UUID.randomUUID() + "@gmail.com",
                "+254712345678"
        );

        ResponseEntity<CustomerResponseDto> create = restTemplate.exchange(
                CUSTOMER_API_PATH, POST, new HttpEntity<>(request), CustomerResponseDto.class);

        assertEquals(HttpStatus.CREATED, create.getStatusCode());
        assertNotNull(create.getBody());

        return Objects.requireNonNull(create.getBody()).id();
    }


    private Long createInvoice(BigDecimal amount) {
        Long customerId = createCustomer();
        InvoiceDto request = new InvoiceDto(
                amount,
                LocalDate.now().plusDays(30),
                customerId
        );

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


    private PaymentDto uniquePaymentRequest(Long invoiceId, BigDecimal amount) {
        return new PaymentDto(
                invoiceId,
                amount,
                LocalDate.now(),
                PaymentMethod.MPESA,
                "TXN-" + UUID.randomUUID()
        );
    }


    private Long createPaymentAndFetchId(PaymentDto request) {
        ResponseEntity<String> create = restTemplate.exchange(
                PAYMENT_API_PATH, POST, new HttpEntity<>(request), String.class);

        assertEquals(HttpStatus.CREATED, create.getStatusCode());

        Payment payment = paymentRepository.findByTransactionNumber(request.transactionNumber());
        assertNotNull(payment);
        return payment.getId();
    }


    @Test
    void shouldCreatePaymentAndPartiallyPayInvoice() {
        // Given
        Long invoiceId = createInvoice(BigDecimal.valueOf(1000));
        PaymentDto request = uniquePaymentRequest(invoiceId, BigDecimal.valueOf(400));

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                PAYMENT_API_PATH, POST, new HttpEntity<>(request), String.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ResponseEntity<InvoiceResponseDto> invoiceResponse = restTemplate.exchange(
                INVOICE_API_PATH + "/" + invoiceId, GET, null, InvoiceResponseDto.class);

        assertNotNull(invoiceResponse.getBody());
        assertEquals(InvoiceStatus.PARTIALLY_PAID, invoiceResponse.getBody().status());
    }


    @Test
    void shouldCreatePaymentAndFullyPayInvoice() {
        // Given
        Long invoiceId = createInvoice(BigDecimal.valueOf(1000));
        PaymentDto request = uniquePaymentRequest(invoiceId, BigDecimal.valueOf(1000));

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                PAYMENT_API_PATH, POST, new HttpEntity<>(request), String.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ResponseEntity<InvoiceResponseDto> invoiceResponse = restTemplate.exchange(
                INVOICE_API_PATH + "/" + invoiceId, GET, null, InvoiceResponseDto.class);

        assertNotNull(invoiceResponse.getBody());
        assertEquals(InvoiceStatus.PAID, invoiceResponse.getBody().status());
    }


    @Test
    void shouldReturnAllPayments() {
        // Given
        createPaymentAndFetchId(uniquePaymentRequest(createInvoice(BigDecimal.valueOf(1000)), BigDecimal.valueOf(300)));
        createPaymentAndFetchId(uniquePaymentRequest(createInvoice(BigDecimal.valueOf(1000)), BigDecimal.valueOf(400)));

        // When
        ResponseEntity<List<PaymentResponseDtoList>> response = restTemplate.exchange(
                PAYMENT_API_PATH, GET, null, new ParameterizedTypeReference<>() {
                });

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull().hasSizeGreaterThanOrEqualTo(2);
    }


    @Test
    void shouldFetchPaymentById() {
        // Given
        PaymentDto request = uniquePaymentRequest(createInvoice(BigDecimal.valueOf(1000)), BigDecimal.valueOf(300));
        Long id = createPaymentAndFetchId(request);

        // When
        ResponseEntity<PaymentResponseDto> response = restTemplate.exchange(
                PAYMENT_API_PATH + "/" + id, GET, null, PaymentResponseDto.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(request.transactionNumber(), response.getBody().transactionNumber());
    }


    @Test
    void shouldUpdatePayment() {
        // Given
        Long id = createPaymentAndFetchId(uniquePaymentRequest(createInvoice(BigDecimal.valueOf(1000)), BigDecimal.valueOf(300)));
        UpdatePaymentDto updateRequest = new UpdatePaymentDto(
                null,
                BigDecimal.valueOf(500),
                LocalDate.now(),
                PaymentMethod.CARD,
                "TXN-" + UUID.randomUUID()
        );

        // When
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                PAYMENT_API_PATH + "/" + id, PUT, new HttpEntity<>(updateRequest), String.class);

        // Then
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        ResponseEntity<PaymentResponseDto> fetched = restTemplate.exchange(
                PAYMENT_API_PATH + "/" + id, GET, null, PaymentResponseDto.class);

        assertNotNull(fetched.getBody());
        assertEquals(updateRequest.transactionNumber(), fetched.getBody().transactionNumber());
    }


    @Test
    void shouldDeletePayment() {
        // Given
        Long id = createPaymentAndFetchId(uniquePaymentRequest(createInvoice(BigDecimal.valueOf(1000)), BigDecimal.valueOf(300)));

        // When
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                PAYMENT_API_PATH + "/" + id, DELETE, null, Void.class);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }


    @Test
    void shouldReturn409WhenCreatingDuplicateTransactionNumber() {
        // Given
        Long invoiceId = createInvoice(BigDecimal.valueOf(1000));
        PaymentDto request = uniquePaymentRequest(invoiceId, BigDecimal.valueOf(300));
        createPaymentAndFetchId(request);

        // When
        ResponseEntity<String> duplicate = restTemplate.exchange(
                PAYMENT_API_PATH, POST, new HttpEntity<>(request), String.class);

        // Then
        assertEquals(HttpStatus.CONFLICT, duplicate.getStatusCode());
    }


    @Test
    void shouldReturn409WhenPaymentExceedsInvoiceAmount() {
        // Given
        Long invoiceId = createInvoice(BigDecimal.valueOf(1000));
        createPaymentAndFetchId(uniquePaymentRequest(invoiceId, BigDecimal.valueOf(800)));
        PaymentDto exceedingPayment = uniquePaymentRequest(invoiceId, BigDecimal.valueOf(300));

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                PAYMENT_API_PATH, POST, new HttpEntity<>(exceedingPayment), String.class);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }


    @Test
    void shouldReturn400WhenCreatingPaymentWithInvalidData() {
        // Given
        PaymentDto badRequest = new PaymentDto(
                null,
                BigDecimal.ZERO,
                LocalDate.now().plusDays(1),
                PaymentMethod.MPESA,
                ""
        );

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                PAYMENT_API_PATH, POST, new HttpEntity<>(badRequest), String.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

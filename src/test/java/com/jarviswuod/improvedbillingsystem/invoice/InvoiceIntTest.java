package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.AbstractTestContainerTest;
import com.jarviswuod.improvedbillingsystem.customer.CustomerDto;
import com.jarviswuod.improvedbillingsystem.customer.CustomerResponseDto;
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
class InvoiceIntTest extends AbstractTestContainerTest {

    private static final String CUSTOMER_API_PATH = "/api/customers";
    private static final String INVOICE_API_PATH = "/api/invoices";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private InvoiceRepository invoiceRepository;


    private Long createCustomer() {
        CustomerDto request = new CustomerDto(
                "Invoice Test User",
                "invoice-user-" + UUID.randomUUID() + "@gmail.com",
                "+254712345678"
        );

        ResponseEntity<CustomerResponseDto> create = restTemplate.exchange(
                CUSTOMER_API_PATH, POST, new HttpEntity<>(request), CustomerResponseDto.class);

        assertEquals(HttpStatus.CREATED, create.getStatusCode());
        assertNotNull(create.getBody());

        return Objects.requireNonNull(create.getBody()).id();
    }


    private InvoiceDto uniqueInvoiceRequest(Long customerId) {
        return new InvoiceDto(
                BigDecimal.valueOf(1500),
                LocalDate.now().plusDays(30),
                customerId
        );
    }


    private Long createInvoiceAndFetchId(InvoiceDto request) {
        ResponseEntity<String> create = restTemplate.exchange(
                INVOICE_API_PATH, POST, new HttpEntity<>(request), String.class);

        assertEquals(HttpStatus.CREATED, create.getStatusCode());

        return invoiceRepository.findAll()
                .stream()
                .filter(invoice -> invoice.getCustomer().getId().equals(request.customerId()))
                .filter(invoice -> invoice.getAmount().compareTo(request.amount()) == 0)
                .map(Invoice::getId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Invoice not found after creation"));
    }


    @Test
    void shouldCreateInvoice() {
        // Given
        Long customerId = createCustomer();
        InvoiceDto request = uniqueInvoiceRequest(customerId);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                INVOICE_API_PATH, POST, new HttpEntity<>(request), String.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertThat(invoiceRepository.findAll())
                .anyMatch(invoice -> invoice.getCustomer().getId().equals(customerId));
    }


    @Test
    void shouldReturnAllInvoices() {
        // Given
        createInvoiceAndFetchId(uniqueInvoiceRequest(createCustomer()));
        createInvoiceAndFetchId(uniqueInvoiceRequest(createCustomer()));

        // When
        ResponseEntity<List<InvoiceResponseDtoList>> response = restTemplate.exchange(
                INVOICE_API_PATH, GET, null, new ParameterizedTypeReference<>() {
                });

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull().hasSizeGreaterThanOrEqualTo(2);
    }


    @Test
    void shouldFetchInvoiceById() {
        // Given
        InvoiceDto request = uniqueInvoiceRequest(createCustomer());
        Long id = createInvoiceAndFetchId(request);

        // When
        ResponseEntity<InvoiceResponseDto> response = restTemplate.exchange(
                INVOICE_API_PATH + "/" + id, GET, null, InvoiceResponseDto.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertThat(response.getBody().amount()).isEqualByComparingTo(request.amount());
    }


    @Test
    void shouldUpdateInvoice() {
        // Given
        Long id = createInvoiceAndFetchId(uniqueInvoiceRequest(createCustomer()));
        InvoiceUpdateDto updateRequest = new InvoiceUpdateDto(
                BigDecimal.valueOf(2000),
                LocalDate.now().plusDays(45),
                null
        );

        // When
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                INVOICE_API_PATH + "/" + id, PUT, new HttpEntity<>(updateRequest), String.class);

        // Then
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        ResponseEntity<InvoiceResponseDto> fetched = restTemplate.exchange(
                INVOICE_API_PATH + "/" + id, GET, null, InvoiceResponseDto.class);

        assertNotNull(fetched.getBody());
        assertThat(fetched.getBody().amount()).isEqualByComparingTo(updateRequest.amount());
    }


    @Test
    void shouldDeleteInvoiceWithoutPayments() {
        // Given
        Long id = createInvoiceAndFetchId(uniqueInvoiceRequest(createCustomer()));

        // When
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                INVOICE_API_PATH + "/" + id, DELETE, null, String.class);

        // Then
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        ResponseEntity<String> fetched = restTemplate.exchange(
                INVOICE_API_PATH + "/" + id, GET, null, String.class);

        assertEquals(HttpStatus.NOT_FOUND, fetched.getStatusCode());
    }


    @Test
    void shouldReturn400WhenCreatingInvoiceWithInvalidData() {
        // Given
        InvoiceDto badRequest = new InvoiceDto(
                BigDecimal.ZERO,
                LocalDate.now().minusDays(1),
                null
        );

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                INVOICE_API_PATH, POST, new HttpEntity<>(badRequest), String.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    void shouldReturn404WhenInvoiceNotFound() {
        // Given
        long nonExistentId = Long.MAX_VALUE;

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                INVOICE_API_PATH + "/" + nonExistentId, GET, null, String.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

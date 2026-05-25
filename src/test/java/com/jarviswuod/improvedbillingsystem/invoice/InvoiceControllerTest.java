package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.customer.CustomerResponseDtoList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InvoiceControllerTest {

    private InvoiceController invoiceController;

    @Mock
    private InvoiceService invoiceService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        invoiceController = new InvoiceController(invoiceService);
    }

    @Test
    void createInvoiceShouldReturnCreatedMessage() {
        // Given
        InvoiceDto dto = new InvoiceDto(BigDecimal.valueOf(1500), LocalDate.now().plusDays(30), 1L);
        doNothing().when(invoiceService).createInvoice(dto);

        // When
        ResponseEntity<String> response = invoiceController.createInvoice(dto);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Invoice created Successfully", response.getBody());
        verify(invoiceService).createInvoice(dto);
    }

    @Test
    void findAllInvoicesShouldReturnInvoices() {
        // Given
        List<InvoiceResponseDtoList> invoices = List.of(new InvoiceResponseDtoList(
                BigDecimal.valueOf(1500),
                LocalDate.now().plusDays(30),
                InvoiceStatus.PENDING,
                new CustomerResponseDtoList(1L, "Jarvis")
        ));
        when(invoiceService.findAllInvoices()).thenReturn(invoices);

        // When
        ResponseEntity<List<InvoiceResponseDtoList>> response = invoiceController.findAllInvoices();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(invoices, response.getBody());
    }

    @Test
    void findInvoiceByIdShouldReturnInvoice() {
        // Given
        long id = 1L;
        InvoiceResponseDto expectedResponse = new InvoiceResponseDto(
                BigDecimal.valueOf(1500),
                LocalDate.now().plusDays(30),
                InvoiceStatus.PENDING,
                new CustomerResponseDtoList(1L, "Jarvis"),
                List.of()
        );
        when(invoiceService.findInvoicesById(id)).thenReturn(expectedResponse);

        // When
        ResponseEntity<InvoiceResponseDto> response = invoiceController.findInvoiceById(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void updateInvoiceShouldReturnOkMessage() {
        // Given
        long id = 1L;
        InvoiceUpdateDto dto = new InvoiceUpdateDto(BigDecimal.valueOf(2000), LocalDate.now().plusDays(40), null);
        doNothing().when(invoiceService).updateInvoice(dto, id);

        // When
        ResponseEntity<String> response = invoiceController.updateInvoice(dto, id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Invoice updated Successfully", response.getBody());
        verify(invoiceService).updateInvoice(dto, id);
    }

    @Test
    void deleteInvoiceShouldReturnOkMessage() {
        // Given
        long id = 1L;
        doNothing().when(invoiceService).deleteInvoiceById(id);

        // When
        ResponseEntity<String> response = invoiceController.deleteInvoice(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Invoice deleted Successfully", response.getBody());
        verify(invoiceService).deleteInvoiceById(id);
    }

    @Test
    void overDueInvoicesShouldReturnOverdueInvoices() {
        // Given
        long customerId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().minusDays(1);
        List<OverdueInvoiceDto> invoices = List.of(new OverdueInvoiceDto(
                1L,
                "Jarvis",
                BigDecimal.valueOf(1500),
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(1000),
                LocalDate.now().minusDays(2),
                InvoiceStatus.OVERDUE
        ));
        when(invoiceService.getOverdueInvoices(customerId, startDate, endDate)).thenReturn(invoices);

        // When
        ResponseEntity<List<OverdueInvoiceDto>> response =
                invoiceController.overDueInvoices(customerId, startDate, endDate);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(invoices, response.getBody());
    }
}

package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.customer.CustomerService;
import com.jarviswuod.improvedbillingsystem.exception.BusinessRuleViolationException;
import com.jarviswuod.improvedbillingsystem.exception.ResourceNotFoundException;
import com.jarviswuod.improvedbillingsystem.payment.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InvoiceServiceTest {

    @InjectMocks
    private InvoiceService invoiceService;

    @Mock
    private InvoiceRepository invoiceRepo;

    @Mock
    private InvoiceMapper invoiceMapper;

    @Mock
    private CustomerService customerService;

    @Captor
    private ArgumentCaptor<Invoice> invoiceCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createInvoiceShouldMapAndSaveInvoice() {
        // Given
        InvoiceDto dto = new InvoiceDto(
                BigDecimal.valueOf(1500),
                LocalDate.now().plusDays(30),
                1L
        );
        Invoice invoice = Invoice.builder()
                .amount(dto.amount())
                .dueDate(dto.dueDate())
                .status(InvoiceStatus.PENDING)
                .build();

        when(invoiceMapper.toInvoice(dto)).thenReturn(invoice);
        when(invoiceRepo.save(invoice)).thenReturn(invoice);

        // When
        invoiceService.createInvoice(dto);

        // Then
        verify(invoiceRepo).save(invoiceCaptor.capture());
        assertEquals(dto.amount(), invoiceCaptor.getValue().getAmount());
        assertEquals(dto.dueDate(), invoiceCaptor.getValue().getDueDate());
    }

    @Test
    void findInvoicesByIdShouldReturnMappedInvoice() {
        // Given
        long id = 1L;
        Invoice invoice = Invoice.builder()
                .amount(BigDecimal.valueOf(1500))
                .dueDate(LocalDate.now().plusDays(30))
                .status(InvoiceStatus.PENDING)
                .payments(List.of())
                .build();
        invoice.setId(id);
        InvoiceResponseDto expectedResponse = new InvoiceResponseDto(
                invoice.getAmount(),
                invoice.getDueDate(),
                invoice.getStatus(),
                null,
                List.of()
        );

        when(invoiceRepo.findById(id)).thenReturn(Optional.of(invoice));
        when(invoiceMapper.toInvoiceResponseDto(invoice)).thenReturn(expectedResponse);

        // When
        InvoiceResponseDto responseDto = invoiceService.findInvoicesById(id);

        // Then
        assertEquals(expectedResponse, responseDto);
        verify(invoiceRepo).findById(id);
    }

    @Test
    void findAllInvoicesShouldReturnMappedInvoices() {
        // Given
        Invoice invoice = Invoice.builder()
                .amount(BigDecimal.valueOf(1500))
                .dueDate(LocalDate.now().plusDays(30))
                .status(InvoiceStatus.PENDING)
                .build();
        InvoiceResponseDtoList expectedResponse = new InvoiceResponseDtoList(
                invoice.getAmount(),
                invoice.getDueDate(),
                invoice.getStatus(),
                null
        );

        when(invoiceRepo.findAll()).thenReturn(List.of(invoice));
        when(invoiceMapper.toInvoiceResponseDtoList(invoice)).thenReturn(expectedResponse);

        // When
        List<InvoiceResponseDtoList> response = invoiceService.findAllInvoices();

        // Then
        assertThat(response).containsExactly(expectedResponse);
    }

    @Test
    void findInvoicesByIdShouldThrowWhenInvoiceDoesNotExist() {
        // Given
        long id = 99L;
        when(invoiceRepo.findById(id)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exp = assertThrows(
                ResourceNotFoundException.class,
                () -> invoiceService.findInvoicesById(id)
        );

        assertEquals("No invoice with id99", exp.getMessage());
        verify(invoiceMapper, never()).toInvoiceResponseDto(any());
    }

    @Test
    void deleteInvoiceByIdShouldDeleteInvoiceWithoutPayments() {
        // Given
        long id = 1L;
        Invoice invoice = Invoice.builder()
                .amount(BigDecimal.valueOf(1500))
                .dueDate(LocalDate.now().plusDays(30))
                .payments(List.of())
                .build();

        when(invoiceRepo.findById(id)).thenReturn(Optional.of(invoice));
        doNothing().when(invoiceRepo).deleteById(id);

        // When
        invoiceService.deleteInvoiceById(id);

        // Then
        verify(invoiceRepo).deleteById(id);
    }

    @Test
    void deleteInvoiceByIdShouldThrowWhenInvoiceHasPayments() {
        // Given
        long id = 1L;
        Invoice invoice = Invoice.builder()
                .amount(BigDecimal.valueOf(1500))
                .dueDate(LocalDate.now().plusDays(30))
                .payments(List.of(Payment.builder().amount(BigDecimal.valueOf(500)).build()))
                .build();

        when(invoiceRepo.findById(id)).thenReturn(Optional.of(invoice));

        // When & Then
        BusinessRuleViolationException exp = assertThrows(
                BusinessRuleViolationException.class,
                () -> invoiceService.deleteInvoiceById(id)
        );

        assertEquals("An invoice with payments cannot be deleted", exp.getMessage());
        verify(invoiceRepo, never()).deleteById(id);
    }

    @Test
    void updateInvoiceShouldMapAndSaveExistingInvoice() {
        // Given
        long id = 1L;
        InvoiceUpdateDto dto = new InvoiceUpdateDto(
                BigDecimal.valueOf(2000),
                LocalDate.now().plusDays(40),
                null
        );
        Invoice existingInvoice = Invoice.builder()
                .amount(BigDecimal.valueOf(1500))
                .dueDate(LocalDate.now().plusDays(30))
                .build();
        Invoice updatedInvoice = Invoice.builder()
                .amount(dto.amount())
                .dueDate(dto.dueDate())
                .build();

        when(invoiceRepo.findById(id)).thenReturn(Optional.of(existingInvoice));
        when(invoiceMapper.toInvoice(dto, existingInvoice)).thenReturn(updatedInvoice);
        when(invoiceRepo.save(updatedInvoice)).thenReturn(updatedInvoice);

        // When
        invoiceService.updateInvoice(dto, id);

        // Then
        verify(invoiceRepo).save(updatedInvoice);
    }

    @Test
    void updateInvoiceShouldSaveInvoiceEntityDirectly() {
        // Given
        Invoice invoice = Invoice.builder()
                .amount(BigDecimal.valueOf(1500))
                .dueDate(LocalDate.now().plusDays(30))
                .status(InvoiceStatus.PENDING)
                .build();
        invoice.setId(1L);

        when(invoiceRepo.save(invoice)).thenReturn(invoice);

        // When
        invoiceService.updateInvoice(invoice);

        // Then
        verify(invoiceRepo).save(invoiceCaptor.capture());
        assertEquals(invoice, invoiceCaptor.getValue());
    }

    @Test
    void getOverdueInvoicesShouldRejectInvalidDateRange() {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.minusDays(1);

        // When & Then
        BusinessRuleViolationException exp = assertThrows(
                BusinessRuleViolationException.class,
                () -> invoiceService.getOverdueInvoices(null, startDate, endDate)
        );

        assertEquals("startDate must not be after endDate", exp.getMessage());
        verify(invoiceRepo, never()).findOverdueInvoices(any(), any(), any(), any());
    }

    @Test
    void getOverdueInvoicesShouldRejectFutureEndDate() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        // When & Then
        BusinessRuleViolationException exp = assertThrows(
                BusinessRuleViolationException.class,
                () -> invoiceService.getOverdueInvoices(null, startDate, endDate)
        );

        assertEquals("endDate must not be in the future", exp.getMessage());
        verify(invoiceRepo, never()).findOverdueInvoices(any(), any(), any(), any());
    }

    @Test
    void getOverdueInvoicesShouldValidateCustomerWhenCustomerIdIsProvided() {
        // Given
        long customerId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().minusDays(1);

        when(invoiceRepo.findOverdueInvoices(any(), any(), any(), any())).thenReturn(List.of());

        // When
        List<OverdueInvoiceDto> response = invoiceService.getOverdueInvoices(customerId, startDate, endDate);

        // Then
        assertThat(response).isEmpty();
        verify(customerService).findActiveCustomerById(customerId);
    }

    @Test
    void getOverdueInvoicesShouldAllowNullCustomerAndNullDates() {
        // Given
        when(invoiceRepo.findOverdueInvoices(any(), any(), isNull(), isNull())).thenReturn(List.of());

        // When
        List<OverdueInvoiceDto> response = invoiceService.getOverdueInvoices(null, null, null);

        // Then
        assertThat(response).isEmpty();
        verify(customerService, never()).findActiveCustomerById(any());
        verify(invoiceRepo).findOverdueInvoices(isNull(), any(), isNull(), isNull());
    }

    @Test
    void getOverdueInvoicesShouldConvertStartDateWhenOnlyStartDateIsProvided() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(10);
        ArgumentCaptor<Instant> startCreatedAtCaptor = ArgumentCaptor.forClass(Instant.class);

        when(invoiceRepo.findOverdueInvoices(any(), any(), any(), isNull())).thenReturn(List.of());

        // When
        List<OverdueInvoiceDto> response = invoiceService.getOverdueInvoices(null, startDate, null);

        // Then
        assertThat(response).isEmpty();
        verify(invoiceRepo).findOverdueInvoices(
                isNull(),
                any(),
                startCreatedAtCaptor.capture(),
                isNull()
        );
        assertEquals(startDate.atStartOfDay(java.time.ZoneOffset.UTC).toInstant(), startCreatedAtCaptor.getValue());
    }

    @Test
    void getOverdueInvoicesShouldConvertEndDateWhenOnlyEndDateIsProvided() {
        // Given
        LocalDate endDate = LocalDate.now().minusDays(1);
        ArgumentCaptor<Instant> endCreatedAtCaptor = ArgumentCaptor.forClass(Instant.class);

        when(invoiceRepo.findOverdueInvoices(any(), any(), isNull(), any())).thenReturn(List.of());

        // When
        List<OverdueInvoiceDto> response = invoiceService.getOverdueInvoices(null, null, endDate);

        // Then
        assertThat(response).isEmpty();
        verify(invoiceRepo).findOverdueInvoices(
                isNull(),
                any(),
                isNull(),
                endCreatedAtCaptor.capture()
        );
        assertEquals(
                endDate.atTime(java.time.LocalTime.MAX).atZone(java.time.ZoneOffset.UTC).toInstant(),
                endCreatedAtCaptor.getValue()
        );
    }

    @Test
    void getOverdueInvoicesShouldPassConvertedDatesAndCustomerId() {
        // Given
        long customerId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().minusDays(1);
        ArgumentCaptor<Instant> startCreatedAtCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> endCreatedAtCaptor = ArgumentCaptor.forClass(Instant.class);

        when(invoiceRepo.findOverdueInvoices(eq(customerId), any(), any(), any())).thenReturn(List.of());

        // When
        List<OverdueInvoiceDto> response = invoiceService.getOverdueInvoices(customerId, startDate, endDate);

        // Then
        assertThat(response).isEmpty();
        verify(customerService).findActiveCustomerById(customerId);
        verify(invoiceRepo).findOverdueInvoices(
                eq(customerId),
                any(),
                startCreatedAtCaptor.capture(),
                endCreatedAtCaptor.capture()
        );
        assertEquals(startDate.atStartOfDay(java.time.ZoneOffset.UTC).toInstant(), startCreatedAtCaptor.getValue());
        assertEquals(
                endDate.atTime(java.time.LocalTime.MAX).atZone(java.time.ZoneOffset.UTC).toInstant(),
                endCreatedAtCaptor.getValue()
        );
    }
}

package com.jarviswuod.improvedbillingsystem.payment;

import com.jarviswuod.improvedbillingsystem.dashboard.BillingSummaryDto;
import com.jarviswuod.improvedbillingsystem.dashboard.CustomersDto;
import com.jarviswuod.improvedbillingsystem.exception.BusinessRuleViolationException;
import com.jarviswuod.improvedbillingsystem.exception.ResourceNotFoundException;
import com.jarviswuod.improvedbillingsystem.invoice.Invoice;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceService;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepo;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private InvoiceService invoiceService;

    @Captor
    private ArgumentCaptor<Payment> paymentCaptor;

    @Captor
    private ArgumentCaptor<Invoice> invoiceCaptor;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createPaymentShouldSavePaymentAndMarkInvoicePartiallyPaid() {
        // Given
        Invoice invoice = Invoice.builder()
                .amount(BigDecimal.valueOf(1000))
                .status(InvoiceStatus.PENDING)
                .payments(List.of())
                .build();
        invoice.setId(1L);
        PaymentDto dto = new PaymentDto(
                invoice.getId(),
                BigDecimal.valueOf(400),
                LocalDate.now(),
                PaymentMethod.MPESA,
                "TXN-1"
        );
        Payment payment = Payment.builder()
                .amount(dto.amount())
                .paymentDate(dto.paymentDate())
                .paymentMethod(dto.paymentMethod())
                .transactionNumber(dto.transactionNumber())
                .invoice(invoice)
                .build();

        when(paymentRepo.findByTransactionNumber(dto.transactionNumber()))
                .thenReturn(null);
        when(paymentMapper.toPayment(dto))
                .thenReturn(payment);
        when(paymentRepo.save(payment))
                .thenReturn(payment);

        // When
        paymentService.createPayment(dto);

        // Then
        verify(invoiceService).updateInvoice(invoiceCaptor.capture());
        verify(paymentRepo).save(paymentCaptor.capture());

        assertEquals(InvoiceStatus.PARTIALLY_PAID, invoiceCaptor.getValue().getStatus());
        assertEquals(dto.transactionNumber(), paymentCaptor.getValue().getTransactionNumber());
    }


    @Test
    void createPaymentShouldSavePaymentAndMarkInvoicePaidWhenFullyPaid() {
        // Given
        Payment existingPayment = Payment.builder().amount(BigDecimal.valueOf(600)).build();
        Invoice invoice = Invoice.builder()
                .amount(BigDecimal.valueOf(1000))
                .status(InvoiceStatus.PARTIALLY_PAID)
                .payments(List.of(existingPayment))
                .build();
        invoice.setId(1L);
        PaymentDto dto = new PaymentDto(
                invoice.getId(),
                BigDecimal.valueOf(400),
                LocalDate.now(),
                PaymentMethod.CARD,
                "TXN-2"
        );
        Payment payment = Payment.builder()
                .amount(dto.amount())
                .paymentDate(dto.paymentDate())
                .paymentMethod(dto.paymentMethod())
                .transactionNumber(dto.transactionNumber())
                .invoice(invoice)
                .build();

        when(paymentRepo.findByTransactionNumber(dto.transactionNumber()))
                .thenReturn(null);
        when(paymentMapper.toPayment(dto))
                .thenReturn(payment);
        when(paymentRepo.save(payment))
                .thenReturn(payment);

        // When
        paymentService.createPayment(dto);

        // Then
        verify(invoiceService).updateInvoice(invoiceCaptor.capture());
        assertEquals(InvoiceStatus.PAID, invoiceCaptor.getValue().getStatus());
    }


    @Test
    void createPaymentShouldThrowWhenTransactionNumberAlreadyExists() {
        // Given
        PaymentDto dto = new PaymentDto(
                1L,
                BigDecimal.valueOf(400),
                LocalDate.now(),
                PaymentMethod.MPESA,
                "TXN-1"
        );

        when(paymentRepo.findByTransactionNumber(dto.transactionNumber()))

                .thenReturn(Payment.builder().transactionNumber(dto.transactionNumber()).build());

        // When & Then
        BusinessRuleViolationException exp = assertThrows(
                BusinessRuleViolationException.class,
                () -> paymentService.createPayment(dto)
        );

        assertEquals("Invalid Transaction Number", exp.getMessage());
        verify(paymentMapper, never()).toPayment(any());
        verify(paymentRepo, never()).save(any());
    }


    @Test
    void createPaymentShouldThrowWhenPaymentExceedsInvoiceAmount() {
        // Given
        Payment existingPayment = Payment.builder().amount(BigDecimal.valueOf(800)).build();
        Invoice invoice = Invoice.builder()
                .amount(BigDecimal.valueOf(1000))
                .status(InvoiceStatus.PARTIALLY_PAID)
                .payments(List.of(existingPayment))
                .build();
        PaymentDto dto = new PaymentDto(
                1L,
                BigDecimal.valueOf(300),
                LocalDate.now(),
                PaymentMethod.MPESA,
                "TXN-1"
        );
        Payment payment = Payment.builder()
                .amount(dto.amount())
                .transactionNumber(dto.transactionNumber())
                .invoice(invoice)
                .build();

        when(paymentRepo.findByTransactionNumber(dto.transactionNumber()))
                .thenReturn(null);
        when(paymentMapper.toPayment(dto))
                .thenReturn(payment);

        // When & Then
        BusinessRuleViolationException exp = assertThrows(
                BusinessRuleViolationException.class,
                () -> paymentService.createPayment(dto)
        );

        assertEquals("Payment would exceed invoice amount", exp.getMessage());
        verify(invoiceService, never()).updateInvoice(any());
        verify(paymentRepo, never()).save(any());
    }


    @Test
    void findPaymentByIdShouldThrowWhenPaymentDoesNotExist() {
        // Given
        long id = 99L;
        when(paymentRepo.findById(id))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exp = assertThrows(
                ResourceNotFoundException.class,
                () -> paymentService.findPaymentById(id)
        );

        assertEquals("No payment with id 99", exp.getMessage());
        verify(paymentMapper, never()).toPaymentResponseDto(any());
    }


    @Test
    void updatePaymentShouldMapAndSavePayment() {
        // Given
        long id = 1L;
        Payment payment = Payment.builder()
                .amount(BigDecimal.valueOf(300))
                .transactionNumber("OLD-TXN")
                .build();
        UpdatePaymentDto dto = new UpdatePaymentDto(
                null,
                BigDecimal.valueOf(500),
                LocalDate.now(),
                PaymentMethod.BANK_TRANSFER,
                "NEW-TXN"
        );
        Payment updatedPayment = Payment.builder()
                .amount(dto.amount())
                .paymentDate(dto.paymentDate())
                .paymentMethod(dto.paymentMethod())
                .transactionNumber(dto.transactionNumber())
                .build();

        when(paymentRepo.findById(id))
                .thenReturn(Optional.of(payment));
        when(paymentMapper.toPayment(dto, payment))
                .thenReturn(updatedPayment);
        when(paymentRepo.save(updatedPayment))
                .thenReturn(updatedPayment);

        // When
        paymentService.updatePayment(dto, id);

        // Then
        verify(paymentRepo).save(updatedPayment);
    }


    @Test
    void deletePaymentByIdShouldDeletePayment() {
        // Given
        long id = 1L;
        doNothing().when(paymentRepo).deleteById(id);

        // When
        paymentService.deletePaymentById(id);

        // Then
        verify(paymentRepo).deleteById(id);
    }


    @Test
    void getSummaryShouldRejectInvalidDateRange() {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.minusDays(1);

        // When & Then
        BusinessRuleViolationException exp = assertThrows(
                BusinessRuleViolationException.class,
                () -> paymentService.getSummary(Instant.now(), Instant.now(), startDate, endDate)
        );

        assertEquals("startDate must not be after endDate", exp.getMessage());
        verify(paymentRepo, never()).getSummary(any(), any(), any(), any());
    }


    @Test
    void findTopCustomersShouldDelegateToRepositoryWhenDateRangeIsValid() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().minusDays(1);
        List<CustomersDto> customers = List.of(new CustomersDto("Jarvis", BigDecimal.valueOf(1000)));

        when(paymentRepo.findTopCustomers(startDate, endDate, 5))
                .thenReturn(customers);

        // When
        List<CustomersDto> response = paymentService.findTopCustomers(startDate, endDate, 5);

        // Then
        assertThat(response).hasSize(1);
        assertEquals("Jarvis", response.get(0).customerName());
    }


    @Test
    void getSummaryShouldDelegateToRepositoryWhenDateRangeIsValid() {
        // Given
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().minusDays(1);
        BillingSummaryDto summary = new BillingSummaryDto(
                1,
                2,
                BigDecimal.valueOf(3000),
                BigDecimal.valueOf(1200),
                BigDecimal.valueOf(1800)
        );

        when(paymentRepo.getSummary(start, end, startDate, endDate))
                .thenReturn(summary);

        // When
        BillingSummaryDto response = paymentService.getSummary(start, end, startDate, endDate);

        // Then
        assertEquals(summary, response);
    }
}

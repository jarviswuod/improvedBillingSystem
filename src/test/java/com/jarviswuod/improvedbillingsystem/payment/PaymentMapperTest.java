package com.jarviswuod.improvedbillingsystem.payment;

import com.jarviswuod.improvedbillingsystem.invoice.Invoice;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceService;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class PaymentMapperTest {

    private PaymentMapper paymentMapper;

    @Mock
    private InvoiceService invoiceService;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        paymentMapper = new PaymentMapper(invoiceService);
    }


    @Test
    void toPaymentShouldMapRequestAndAttachInvoice() {
        // Given
        long invoiceId = 1L;
        Invoice invoice = Invoice.builder()
                .amount(BigDecimal.valueOf(1500))
                .status(InvoiceStatus.PENDING)
                .build();
        invoice.setId(invoiceId);
        PaymentDto dto = new PaymentDto(
                invoiceId,
                BigDecimal.valueOf(500),
                LocalDate.now(),
                PaymentMethod.MPESA,
                "TXN-1"
        );

        when(invoiceService.getInvoiceById(invoiceId)).thenReturn(invoice);

        // When
        Payment payment = paymentMapper.toPayment(dto);

        // Then
        assertEquals(dto.amount(), payment.getAmount());
        assertEquals(dto.paymentDate(), payment.getPaymentDate());
        assertEquals(dto.paymentMethod(), payment.getPaymentMethod());
        assertEquals(dto.transactionNumber(), payment.getTransactionNumber());
        assertSame(invoice, payment.getInvoice());
    }


    @Test
    void toPaymentUpdateShouldKeepInvoiceWhenInvoiceIdIsNull() {
        // Given
        Invoice invoice = Invoice.builder().amount(BigDecimal.valueOf(1500)).build();
        invoice.setId(1L);
        Payment payment = Payment.builder()
                .amount(BigDecimal.valueOf(300))
                .paymentDate(LocalDate.now().minusDays(1))
                .paymentMethod(PaymentMethod.CARD)
                .transactionNumber("OLD-TXN")
                .invoice(invoice)
                .build();
        UpdatePaymentDto dto = new UpdatePaymentDto(
                null,
                BigDecimal.valueOf(700),
                LocalDate.now(),
                PaymentMethod.BANK_TRANSFER,
                "NEW-TXN"
        );

        // When
        Payment updatedPayment = paymentMapper.toPayment(dto, payment);

        // Then
        assertSame(payment, updatedPayment);
        assertEquals(dto.amount(), updatedPayment.getAmount());
        assertEquals(dto.paymentDate(), updatedPayment.getPaymentDate());
        assertEquals(dto.paymentMethod(), updatedPayment.getPaymentMethod());
        assertEquals(dto.transactionNumber(), updatedPayment.getTransactionNumber());
        assertSame(invoice, updatedPayment.getInvoice());
        verify(invoiceService, never()).getInvoiceById(org.mockito.ArgumentMatchers.anyLong());
    }


    @Test
    void toPaymentResponseDtoShouldMapEntityFields() {
        // Given
        Invoice invoice = Invoice.builder().amount(BigDecimal.valueOf(1500)).build();
        invoice.setId(10L);
        Payment payment = Payment.builder()
                .amount(BigDecimal.valueOf(500))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.MPESA)
                .transactionNumber("TXN-1")
                .invoice(invoice)
                .build();
        payment.setId(1L);

        // When
        PaymentResponseDto responseDto = paymentMapper.toPaymentResponseDto(payment);

        // Then
        assertEquals(payment.getId(), responseDto.id());
        assertEquals(payment.getAmount(), responseDto.amount());
        assertEquals(payment.getPaymentMethod(), responseDto.paymentMethod());
        assertEquals(payment.getTransactionNumber(), responseDto.transactionNumber());
        assertEquals(payment.getPaymentDate(), responseDto.paymentDate());
        assertEquals(invoice.getId(), responseDto.invoiceId());
    }
}

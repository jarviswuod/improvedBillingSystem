package com.jarviswuod.improvedbillingsystem.payment;

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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentControllerTest {

    private PaymentController paymentController;

    @Mock
    private PaymentService paymentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        paymentController = new PaymentController(paymentService);
    }

    @Test
    void createPaymentShouldReturnCreatedMessage() {
        // Given
        PaymentDto dto = new PaymentDto(
                1L,
                BigDecimal.valueOf(500),
                LocalDate.now(),
                PaymentMethod.MPESA,
                "TXN-1"
        );
        doNothing().when(paymentService).createPayment(dto);

        // When
        ResponseEntity<String> response = paymentController.createPayment(dto);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Payment Successfully Done!", response.getBody());
        verify(paymentService).createPayment(dto);
    }

    @Test
    void findAllPaymentsShouldReturnPayments() {
        // Given
        List<PaymentResponseDtoList> payments = List.of(new PaymentResponseDtoList(
                BigDecimal.valueOf(500),
                PaymentMethod.MPESA,
                "TXN-1",
                LocalDate.now(),
                1L
        ));
        when(paymentService.findAllPayments()).thenReturn(payments);

        // When
        ResponseEntity<List<PaymentResponseDtoList>> response = paymentController.findAllPayments();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(payments, response.getBody());
    }

    @Test
    void findPaymentByIdShouldReturnPayment() {
        // Given
        long id = 1L;
        PaymentResponseDto expectedResponse = new PaymentResponseDto(
                id,
                BigDecimal.valueOf(500),
                PaymentMethod.MPESA,
                "TXN-1",
                LocalDate.now(),
                10L
        );
        when(paymentService.findPaymentById(id)).thenReturn(expectedResponse);

        // When
        ResponseEntity<PaymentResponseDto> response = paymentController.findPaymentById(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void updatePaymentShouldReturnOkMessage() {
        // Given
        long id = 1L;
        UpdatePaymentDto dto = new UpdatePaymentDto(
                null,
                BigDecimal.valueOf(700),
                LocalDate.now(),
                PaymentMethod.CARD,
                "TXN-2"
        );
        doNothing().when(paymentService).updatePayment(dto, id);

        // When
        ResponseEntity<String> response = paymentController.updatePayment(dto, id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Payment updated successfully", response.getBody());
        verify(paymentService).updatePayment(dto, id);
    }

    @Test
    void deletePaymentByIdShouldReturnNoContent() {
        // Given
        long id = 1L;
        doNothing().when(paymentService).deletePaymentById(id);

        // When
        ResponseEntity<Void> response = paymentController.deletePaymentById(id);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(paymentService).deletePaymentById(id);
    }
}

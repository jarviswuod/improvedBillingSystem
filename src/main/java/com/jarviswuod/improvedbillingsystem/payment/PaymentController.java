package com.jarviswuod.improvedbillingsystem.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> createPayment(
            @Valid @RequestBody PaymentDto paymentDto
    ) {
        paymentService.createPayment(paymentDto);
        return new ResponseEntity<>("Payment Successfully Done!", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDtoList>> findAllPayments() {
        return ResponseEntity
                .ok(paymentService.findAllPayments());
    }

    @GetMapping("{payment-id}")
    public ResponseEntity<PaymentResponseDto> findPaymentById(
            @PathVariable("payment-id") Long paymentId
    ) {
        return ResponseEntity
                .ok(paymentService.findPaymentById(paymentId));
    }
}

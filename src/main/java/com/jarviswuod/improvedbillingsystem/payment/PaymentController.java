package com.jarviswuod.improvedbillingsystem.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public String createPayment(
            @Valid @RequestBody PaymentDto paymentDto
    ) {
        paymentService.createPayment(paymentDto);
        return "Payment Successfully Done!";
    }

    @GetMapping
    public List<PaymentResponseDtoList> findAllPayments() {
        return paymentService.findAllPayments();
    }

    @GetMapping("{payment-id}")
    public PaymentResponseDto findPaymentById(
            @PathVariable("payment-id") Long paymentId
    ) {
        return paymentService.findPaymentById(paymentId);
    }
}

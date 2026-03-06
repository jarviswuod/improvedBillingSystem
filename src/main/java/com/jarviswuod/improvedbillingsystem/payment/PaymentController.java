package com.jarviswuod.improvedbillingsystem.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public Payment createPayment(
            @RequestBody PaymentDto paymentDto
    ) {
        return paymentService.createPayment(paymentDto);
    }

    @GetMapping
    public List<Payment> findAllPayments() {
        return paymentService.findAllPayments();
    }

    @GetMapping("{payment-id}")
    public Payment findPaymentById(
            @PathVariable("payment-id") Long paymentId
    ) {
        return paymentService.findPaymentById(paymentId);
    }
}

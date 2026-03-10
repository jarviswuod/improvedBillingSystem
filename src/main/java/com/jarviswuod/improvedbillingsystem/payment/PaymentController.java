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
        return ResponseEntity.status(HttpStatus.CREATED).body("Payment Successfully Done!");
    }


    @GetMapping
    public ResponseEntity<List<PaymentResponseDtoList>> findAllPayments() {
        return ResponseEntity.ok(paymentService.findAllPayments());
    }


    @GetMapping("{id}")
    public ResponseEntity<PaymentResponseDto> findPaymentById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(paymentService.findPaymentById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updatePayment(
            @Valid @RequestBody UpdatePaymentDto dto,
            @PathVariable Long id
    ) {
        paymentService.updatePayment(dto, id);
        return ResponseEntity.ok("Payment updated successfully");
    }


    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletePaymentById(
            @PathVariable Long id
    ) {
        paymentService.deletePaymentById(id);
        return ResponseEntity.noContent().build();
    }
}

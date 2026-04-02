package com.jarviswuod.improvedbillingsystem.payment;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment operations and invoice status updates")
@SecurityRequirement(name = "bearer-jwt")
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping
    @Operation(
            summary = "Create a payment",
            description = "Creates a payment for an invoice and updates the invoice status accordingly."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment created"),
            @ApiResponse(responseCode = "404", description = "Invoice not found"),
            @ApiResponse(responseCode = "409", description = "Business rule violation (invalid transaction number or exceeds invoice amount)"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<String> createPayment(
            @Valid @RequestBody PaymentDto paymentDto
    ) {
        paymentService.createPayment(paymentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Payment Successfully Done!");
    }


    @GetMapping
    @Operation(summary = "List payments", description = "Returns all payments.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payments retrieved")
    })
    public ResponseEntity<List<PaymentResponseDtoList>> findAllPayments() {
        return ResponseEntity.ok(paymentService.findAllPayments());
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get payment by id", description = "Retrieves a payment by its id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment retrieved"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentResponseDto> findPaymentById(
            @Parameter(description = "Payment id", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(paymentService.findPaymentById(id));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update payment", description = "Updates a payment's fields (amount, date, method, transaction number, invoice).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment updated"),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Business rule violation")
    })
    public ResponseEntity<String> updatePayment(
            @Valid @RequestBody UpdatePaymentDto dto,
            @Parameter(description = "Payment id", example = "1")
            @PathVariable Long id
    ) {
        paymentService.updatePayment(dto, id);
        return ResponseEntity.ok("Payment updated successfully");
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment", description = "Deletes a payment by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Payment deleted"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<Void> deletePaymentById(
            @Parameter(description = "Payment id", example = "1")
            @PathVariable Long id
    ) {
        paymentService.deletePaymentById(id);
        return ResponseEntity.noContent().build();
    }
}

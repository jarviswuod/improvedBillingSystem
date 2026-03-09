package com.jarviswuod.improvedbillingsystem.invoice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<String> createInvoice(
            @Valid @RequestBody InvoiceDto invoiceDto
    ) {
        invoiceService.save(invoiceDto);
        return new ResponseEntity<>("Invoice created Successfully", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponseDtoList>> findAllInvoices() {
        return ResponseEntity
                .ok(invoiceService.findAllInvoices());
    }

    @GetMapping("/{invoice-id}")
    public ResponseEntity<InvoiceResponseDto> findInvoiceById(
            @PathVariable("invoice-id") Long invoiceId
    ) {
        return ResponseEntity
                .ok(invoiceService.findInvoicesById(invoiceId));
    }

    @DeleteMapping("/{invoice-id}")
    public ResponseEntity<String> deleteInvoice(
            @PathVariable("invoice-id") Long invoiceId
    ) {
        invoiceService.deleteInvoiceById(invoiceId);
        return ResponseEntity
                .ok("Invoice deleted Successfully");
    }
}

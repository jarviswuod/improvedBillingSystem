package com.jarviswuod.improvedbillingsystem.invoice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
        invoiceService.createInvoice(invoiceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Invoice created Successfully");
    }


    @GetMapping
    public ResponseEntity<List<InvoiceResponseDtoList>> findAllInvoices() {
        return ResponseEntity.ok(invoiceService.findAllInvoices());
    }


    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponseDto> findInvoiceById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(invoiceService.findInvoicesById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updateInvoice(
            @Valid @RequestBody InvoiceUpdateDto dto,
            @PathVariable Long id
    ) {
        invoiceService.updateInvoice(dto, id);
        return ResponseEntity.ok().body("Invoice updated Successfully");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInvoice(
            @PathVariable Long id
    ) {
        invoiceService.deleteInvoiceById(id);
        return ResponseEntity.ok("Invoice deleted Successfully");
    }


    @GetMapping("/overdue")
    public ResponseEntity<List<OverdueInvoiceDto>> overDueInvoices(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(invoiceService.getOverdueInvoices(customerId, startDate, endDate));
    }
}

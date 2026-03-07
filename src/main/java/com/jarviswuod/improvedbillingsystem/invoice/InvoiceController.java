package com.jarviswuod.improvedbillingsystem.invoice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public String createInvoice(
            @Valid @RequestBody InvoiceDto invoiceDto
    ) {
        invoiceService.save(invoiceDto);
        return "Invoice created Successfully";
    }

    @GetMapping
    public List<InvoiceResponseDtoList> findAllInvoices() {
        return invoiceService.findAllInvoices();
    }

    @GetMapping("/{invoice-id}")
    public InvoiceResponseDto findInvoiceById(
            @PathVariable("invoice-id") Long invoiceId
    ) {
        return invoiceService.findInvoicesById(invoiceId);
    }

    @DeleteMapping("/{invoice-id}")
    public String deleteInvoice(
            @PathVariable("invoice-id") Long invoiceId
    ) {
        invoiceService.deleteInvoiceById(invoiceId);
        return "Invoice deleted Successfully";
    }
}

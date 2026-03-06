package com.jarviswuod.improvedbillingsystem.invoice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public Invoice createInvoice(
            @RequestBody InvoiceDto invoiceDto
    ) {
        return invoiceService.save(invoiceDto);
    }

    @GetMapping
    public List<Invoice> findAllInvoices() {
        return invoiceService.findAllInvoices();
    }

    @GetMapping("/{invoice-id}")
    public Invoice findInvoiceById(@PathVariable("invoice-id") Long invoiceId) {
        return invoiceService.findInvoicesById(invoiceId);
    }

    @DeleteMapping("/{invoice-id}")
    public void deleteInvoice(@PathVariable("invoice-id") Long invoiceId) {
        invoiceService.deleteInvoiceById(invoiceId);
    }
}

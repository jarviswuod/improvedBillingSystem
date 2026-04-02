package com.jarviswuod.improvedbillingsystem.invoice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Invoices", description = "Invoice CRUD and overdue queries")
@SecurityRequirement(name = "bearer-jwt")
public class InvoiceController {

    private final InvoiceService invoiceService;


    @PostMapping
    @Operation(summary = "Create an invoice", description = "Creates a new invoice for an existing active customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Invoice created"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<String> createInvoice(
            @Valid @RequestBody InvoiceDto invoiceDto
    ) {
        invoiceService.createInvoice(invoiceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Invoice created Successfully");
    }


    @GetMapping
    @Operation(summary = "List invoices", description = "Returns all invoices.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoices retrieved")
    })
    public ResponseEntity<List<InvoiceResponseDtoList>> findAllInvoices() {
        return ResponseEntity.ok(invoiceService.findAllInvoices());
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by id", description = "Retrieves an invoice by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoice retrieved"),
            @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    public ResponseEntity<InvoiceResponseDto> findInvoiceById(
            @Parameter(description = "Invoice id", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(invoiceService.findInvoicesById(id));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update invoice", description = "Updates an invoice's amount, due date, or customer (optional).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoice updated"),
            @ApiResponse(responseCode = "404", description = "Invoice not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<String> updateInvoice(
            @Valid @RequestBody InvoiceUpdateDto dto,
            @Parameter(description = "Invoice id", example = "1")
            @PathVariable Long id
    ) {
        invoiceService.updateInvoice(dto, id);
        return ResponseEntity.ok().body("Invoice updated Successfully");
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete invoice", description = "Deletes an invoice. Fails if the invoice has payments.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoice deleted"),
            @ApiResponse(responseCode = "404", description = "Invoice not found"),
            @ApiResponse(responseCode = "409", description = "Invoice cannot be deleted because it has payments")
    })
    public ResponseEntity<String> deleteInvoice(
            @Parameter(description = "Invoice id", example = "1")
            @PathVariable Long id
    ) {
        invoiceService.deleteInvoiceById(id);
        return ResponseEntity.ok("Invoice deleted Successfully");
    }


    @GetMapping("/overdue")
    @Operation(summary = "List overdue invoices", description = "Returns invoices that are overdue and not fully paid.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Overdue invoices retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    public ResponseEntity<List<OverdueInvoiceDto>> overDueInvoices(
            @Parameter(description = "Filter by customer id (optional)")
            @RequestParam(required = false) Long customerId,
            @Parameter(description = "Filter by invoice created start date (optional)", example = "2024-01-01")
            @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Filter by invoice created end date (optional)", example = "2024-12-31")
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(invoiceService.getOverdueInvoices(customerId, startDate, endDate));
    }
}

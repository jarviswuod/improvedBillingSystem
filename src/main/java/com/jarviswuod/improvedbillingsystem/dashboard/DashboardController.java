package com.jarviswuod.improvedbillingsystem.dashboard;

import com.jarviswuod.improvedbillingsystem.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Billing summary and reporting endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class DashboardController {

    private final PaymentService paymentService;


    /*
        @GetMapping("/summary")
        public ResponseEntity<BillingSummaryDto> getSummary(
                @RequestParam(required = false) LocalDate startDate,
                @RequestParam(required = false) LocalDate endDate,
                @RequestParam(required = false) boolean customersFilter,
                @RequestParam(required = false) boolean invoicesFilter,
                @RequestParam(required = false) boolean paymentsFilter
        ) {
            return ResponseEntity.ok(paymentService.getSummary(startDate, endDate, customersFilter, invoicesFilter, paymentsFilter));
        }
   */

    @GetMapping("/summary")
    @Operation(summary = "Billing summary", description = "Returns high-level billing KPIs for a date range.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Summary retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    public ResponseEntity<BillingSummaryDto> getSummary(
            @Parameter(description = "Invoice created start date (optional)", example = "2024-01-01")
            @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Invoice created end date (optional)", example = "2024-12-31")
            @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "Created/paid instant start (optional)")
            @RequestParam(required = false) Instant start,
            @Parameter(description = "Created/paid instant end (optional)")
            @RequestParam(required = false) Instant end
    ) {
        return ResponseEntity.ok(paymentService.getSummary(start, end, startDate, endDate));
    }


    @GetMapping("/top-customers")
    @Operation(summary = "Top customers", description = "Returns customers ranked by total paid within a date range.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top customers retrieved")
    })
    public ResponseEntity<List<CustomersDto>> topCustomers(
            @Parameter(description = "Start date (optional)", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (optional)", example = "2024-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Max results", example = "5")
            @RequestParam(defaultValue = "5") Integer limit
    ) {
        return ResponseEntity.ok(paymentService.findTopCustomers(startDate, endDate, limit));
    }


    @GetMapping("/monthly-revenue")
    @Operation(summary = "Monthly revenue", description = "Returns aggregated monthly paid totals for a date range.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Monthly revenue retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    public ResponseEntity<List<MonthlyRevenueDto>> monthlyRevenue(
            @Parameter(description = "Start date (optional)", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (optional)", example = "2024-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(paymentService.findMonthlyRevenue(startDate, endDate));
    }
}

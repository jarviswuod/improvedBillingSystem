package com.jarviswuod.improvedbillingsystem.dashboard;

import com.jarviswuod.improvedbillingsystem.payment.PaymentService;
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
public class DashboardController {

    private final DashboardService dashboardService;
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
    public ResponseEntity<BillingSummaryDto> getSummary(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Instant start,
            @RequestParam(required = false) Instant end
    ) {
        return ResponseEntity.ok(paymentService.getSummary(start, end, startDate, endDate));
    }


    @GetMapping("/top-customers")
    public ResponseEntity<List<CustomersDto>> topCustomers(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "5") Integer limit
    ) {
        return ResponseEntity.ok(paymentService.findTopCustomers(startDate, endDate, limit));
    }


    @GetMapping("/monthly-revenue")
    public ResponseEntity<List<MonthlyRevenueDto>> monthlyRevenue(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(paymentService.findMonthlyRevenue(startDate, endDate));
    }
}

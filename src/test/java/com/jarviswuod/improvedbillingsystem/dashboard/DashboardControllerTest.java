package com.jarviswuod.improvedbillingsystem.dashboard;

import com.jarviswuod.improvedbillingsystem.payment.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DashboardControllerTest {

    private DashboardController dashboardController;

    @Mock
    private PaymentService paymentService;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        dashboardController = new DashboardController(paymentService);
    }


    @Test
    void getSummaryShouldReturnBillingSummary() {
        // Given
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().minusDays(1);
        BillingSummaryDto summary = new BillingSummaryDto(
                2,
                4,
                BigDecimal.valueOf(4000),
                BigDecimal.valueOf(1500),
                BigDecimal.valueOf(2500)
        );

        when(paymentService.getSummary(start, end, startDate, endDate))
                .thenReturn(summary);

        // When
        ResponseEntity<BillingSummaryDto> response =
                dashboardController.getSummary(startDate, endDate, start, end);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(summary, response.getBody());
        verify(paymentService).getSummary(start, end, startDate, endDate);
    }


    @Test
    void topCustomersShouldReturnCustomers() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().minusDays(1);
        int limit = 5;
        List<CustomersDto> customers = List.of(new CustomersDto("Jarvis", BigDecimal.valueOf(1500)));

        when(paymentService.findTopCustomers(startDate, endDate, limit))
                .thenReturn(customers);

        // When
        ResponseEntity<List<CustomersDto>> response =
                dashboardController.topCustomers(startDate, endDate, limit);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customers, response.getBody());
        verify(paymentService).findTopCustomers(startDate, endDate, limit);
    }


    @Test
    void monthlyRevenueShouldReturnRevenue() {
        // Given
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        List<MonthlyRevenueDto> revenue = List.of(new MonthlyRevenueDto("2026-05", BigDecimal.valueOf(3000)));

        when(paymentService.findMonthlyRevenue(startDate, endDate))
                .thenReturn(revenue);

        // When
        ResponseEntity<List<MonthlyRevenueDto>> response =
                dashboardController.monthlyRevenue(startDate, endDate);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(revenue, response.getBody());
        verify(paymentService).findMonthlyRevenue(startDate, endDate);
    }
}

package com.jarviswuod.improvedbillingsystem.invoice;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OverdueInvoiceDtoTest {

    @Test
    void getDaysOverdueShouldReturnDaysBetweenDueDateAndToday() {
        // Given
        OverdueInvoiceDto dto = new OverdueInvoiceDto(
                1L,
                "Jarvis",
                BigDecimal.valueOf(1500),
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(1000),
                LocalDate.now().minusDays(3),
                InvoiceStatus.OVERDUE
        );

        // When
        long daysOverdue = dto.getDaysOverdue();

        // Then
        assertEquals(3, daysOverdue);
    }

    @Test
    void getDaysOverdueShouldReturnZeroWhenDueDateIsNull() {
        // Given
        OverdueInvoiceDto dto = new OverdueInvoiceDto(
                1L,
                "Jarvis",
                BigDecimal.valueOf(1500),
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(1000),
                null,
                InvoiceStatus.OVERDUE
        );

        // When
        long daysOverdue = dto.getDaysOverdue();

        // Then
        assertEquals(0, daysOverdue);
    }

    @Test
    void stringStatusConstructorShouldConvertStatusToEnum() {
        // Given & When
        OverdueInvoiceDto dto = new OverdueInvoiceDto(
                1L,
                "Jarvis",
                BigDecimal.valueOf(1500),
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(1000),
                LocalDate.now().minusDays(1),
                "OVERDUE"
        );

        // Then
        assertEquals(InvoiceStatus.OVERDUE, dto.status());
    }
}

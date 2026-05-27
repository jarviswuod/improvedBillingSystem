package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.customer.Customer;
import com.jarviswuod.improvedbillingsystem.customer.CustomerMapper;
import com.jarviswuod.improvedbillingsystem.customer.CustomerResponseDtoList;
import com.jarviswuod.improvedbillingsystem.customer.CustomerService;
import com.jarviswuod.improvedbillingsystem.payment.Payment;
import com.jarviswuod.improvedbillingsystem.payment.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class InvoiceMapperTest {

    private InvoiceMapper invoiceMapper;

    @Mock
    private CustomerService customerService;

    @Mock
    private CustomerMapper customerMapper;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        invoiceMapper = new InvoiceMapper(customerService, customerMapper);
    }


    @Test
    void toInvoiceShouldMapRequestAndAttachCustomer() {
        // Given
        long customerId = 1L;
        Customer customer = Customer.builder()
                .name("Jarvis")
                .email("jarvis@example.com")
                .build();
        customer.setId(customerId);
        InvoiceDto dto = new InvoiceDto(
                BigDecimal.valueOf(1500),
                LocalDate.now().plusDays(30),
                customerId
        );

        when(customerService.findActiveCustomerById(customerId))
                .thenReturn(customer);

        // When
        Invoice invoice = invoiceMapper.toInvoice(dto);

        // Then
        assertEquals(dto.amount(), invoice.getAmount());
        assertEquals(dto.dueDate(), invoice.getDueDate());
        assertEquals(InvoiceStatus.PENDING, invoice.getStatus());
        assertSame(customer, invoice.getCustomer());
    }


    @Test
    void toInvoiceUpdateShouldMutateExistingInvoiceAndKeepCustomerWhenCustomerIdIsNull() {
        // Given
        Customer customer = Customer.builder()
                .name("Jarvis")
                .email("jarvis@example.com")
                .build();
        Invoice invoice = Invoice.builder()
                .amount(BigDecimal.valueOf(1000))
                .dueDate(LocalDate.now().plusDays(10))
                .customer(customer)
                .build();
        InvoiceUpdateDto dto = new InvoiceUpdateDto(
                BigDecimal.valueOf(2000),
                LocalDate.now().plusDays(20),
                null
        );

        // When
        Invoice updatedInvoice = invoiceMapper.toInvoice(dto, invoice);

        // Then
        assertSame(invoice, updatedInvoice);
        assertEquals(dto.amount(), updatedInvoice.getAmount());
        assertEquals(dto.dueDate(), updatedInvoice.getDueDate());
        assertSame(customer, updatedInvoice.getCustomer());
        verify(customerService, never()).findActiveCustomerById(org.mockito.ArgumentMatchers.anyLong());
    }


    @Test
    void toInvoiceResponseDtoShouldMapCustomerAndPayments() {
        // Given
        Customer customer = Customer.builder()
                .name("Jarvis")
                .email("jarvis@example.com")
                .build();
        customer.setId(1L);
        Invoice invoice = Invoice.builder()
                .amount(BigDecimal.valueOf(1500))
                .dueDate(LocalDate.now().plusDays(30))
                .status(InvoiceStatus.PARTIALLY_PAID)
                .customer(customer)
                .build();
        Payment payment = Payment.builder()
                .amount(BigDecimal.valueOf(500))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.MPESA)
                .transactionNumber("TXN-1")
                .invoice(invoice)
                .build();
        invoice.setPayments(List.of(payment));

        when(customerMapper.toCustomerResponseDtoList(customer))
                .thenReturn(new CustomerResponseDtoList(customer.getId(), customer.getName()));

        // When
        InvoiceResponseDto responseDto = invoiceMapper.toInvoiceResponseDto(invoice);

        // Then
        assertEquals(invoice.getAmount(), responseDto.amount());
        assertEquals(invoice.getDueDate(), responseDto.dueData());
        assertEquals(invoice.getStatus(), responseDto.status());
        assertEquals(customer.getName(), responseDto.customer().name());
        assertEquals(1, responseDto.payments().size());
        assertEquals(payment.getAmount(), responseDto.payments().get(0).amount());
    }
}

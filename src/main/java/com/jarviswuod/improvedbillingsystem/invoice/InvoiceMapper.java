package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.customer.Customer;
import com.jarviswuod.improvedbillingsystem.customer.CustomerMapper;
import com.jarviswuod.improvedbillingsystem.customer.CustomerService;
import com.jarviswuod.improvedbillingsystem.payment.Payment;
import com.jarviswuod.improvedbillingsystem.payment.PaymentInvoiceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceMapper {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;


    public Invoice toInvoice(InvoiceDto invoiceDto) {

        Invoice invoice = new Invoice();
        invoice.setDueDate(invoiceDto.dueDate());
        invoice.setAmount(invoiceDto.amount());
        invoice.setBalance(invoiceDto.amount());
        invoice.setStatus(InvoiceStatus.PENDING);

        Long customerId = invoiceDto.customerId();
        Customer customer = customerService.findActiveCustomerById(customerId);

        invoice.setCustomer(customer);

        return invoice;
    }


    public Invoice toInvoice(InvoiceUpdateDto dto, Invoice invoice) {

        invoice.setDueDate(dto.dueDate());
        invoice.setAmount(dto.amount());
        invoice.setBalance(dto.amount());

        Long customerId = dto.customerId();
        if (customerId != null) {
            Customer customer = customerService.findActiveCustomerById(customerId);

            invoice.setCustomer(customer);
        }
        return invoice;
    }


    public InvoiceResponseDtoList toInvoiceResponseDtoList(Invoice invoice) {

        return new InvoiceResponseDtoList(
                invoice.getAmount(),
                invoice.getDueDate(),
                invoice.getStatus(),
                customerMapper.toCustomerResponseDtoList(invoice.getCustomer())
        );
    }


    public PaymentInvoiceResponseDto toPaymentInvoiceResponseDto(Payment payment) {
        return new PaymentInvoiceResponseDto(
                payment.getAmount(),
                payment.getPaymentDate()
        );
    }


    public InvoiceResponseDto toInvoiceResponseDto(Invoice invoice) {

        List<PaymentInvoiceResponseDto> paymentInvoiceResponseDto = invoice.getPayments()
                .stream()
                .map(this::toPaymentInvoiceResponseDto)
                .collect(Collectors.toList());

        return new InvoiceResponseDto(
                invoice.getAmount(),
                invoice.getDueDate(),
                invoice.getStatus(),
                customerMapper.toCustomerResponseDtoList(invoice.getCustomer()),
                paymentInvoiceResponseDto

        );
    }


    public OverdueInvoiceDto toOverdueInvoiceDto(Invoice invoice) {

        BigDecimal amountPaid = invoice.getPayments().stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = invoice.getAmount().subtract(amountPaid);

        int daysOverdue = (int) ChronoUnit.DAYS.between(
                invoice.getDueDate(),
                LocalDate.now()
        );

        return new OverdueInvoiceDto(
                invoice.getId(),
                invoice.getCustomer().getName(),
                invoice.getAmount(),
                amountPaid,
                balance,
                invoice.getDueDate(),
                daysOverdue,
                InvoiceStatus.OVERDUE
        );
    }

}

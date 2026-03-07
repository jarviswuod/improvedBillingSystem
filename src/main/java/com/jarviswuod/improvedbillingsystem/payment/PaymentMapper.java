package com.jarviswuod.improvedbillingsystem.payment;

import com.jarviswuod.improvedbillingsystem.invoice.Invoice;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentMapper {

    private final InvoiceService invoiceService;

    public Payment toPayment(PaymentDto paymentDto) {

        Payment payment = new Payment();
        payment.setAmount(paymentDto.amount());
        payment.setPaymentMethod(paymentDto.paymentMethod());
        payment.setPaymentDate(paymentDto.paymentDate());
        payment.setTransactionNumber(paymentDto.transactionNumber());

        Long invoiceId = paymentDto.invoiceId();

        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
        payment.setInvoice(invoice);

        return payment;
    }

    public PaymentResponseDtoList toPaymentResponseDtoList(Payment payment) {

        return new PaymentResponseDtoList(

                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getTransactionNumber(),
                payment.getPaymentDate(),
                payment.getInvoice().getId()
        );
    }

    public PaymentResponseDto toPaymentResponseDto(Payment payment) {

        return new PaymentResponseDto(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getTransactionNumber(),
                payment.getPaymentDate(),
                payment.getInvoice().getId()
        );
    }

}

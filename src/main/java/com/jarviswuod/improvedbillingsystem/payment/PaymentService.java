package com.jarviswuod.improvedbillingsystem.payment;

import com.jarviswuod.improvedbillingsystem.exception.BusinessRuleViolationException;
import com.jarviswuod.improvedbillingsystem.exception.ResourceNotFoundException;
import com.jarviswuod.improvedbillingsystem.invoice.Invoice;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceService;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final InvoiceService invoiceService;

    public Payment createPayment(PaymentDto paymentDto) {
        Payment payment = toPayment(paymentDto);

        invoiceStatusUpdate(payment);

        return paymentRepo.save(payment);
    }


    private void invoiceStatusUpdate(Payment payment) {
        Invoice invoice = payment.getInvoice();

        Double invoiceBalance = invoice.getBalance();
        invoiceBalance -= payment.getAmount();
        InvoiceStatus status = invoice.getStatus();

        if (invoiceBalance < 0) {
            throw new BusinessRuleViolationException("Payment must not exceed the invoice amount");
        } else if (invoiceBalance == 0) {
            status = InvoiceStatus.PAID;
        } else if (invoiceBalance > 0) {
            status = InvoiceStatus.PENDING;
        }

        invoice.setStatus(status);
    }

    private Payment toPayment(PaymentDto paymentDto) {

        Payment payment = new Payment();
        payment.setAmount(paymentDto.amount());
        payment.setPaymentMethod(paymentDto.paymentMethod());
        payment.setPaymentDate(paymentDto.paymentDate());
        payment.setTransactionNumber(paymentDto.transactionNumber());

        Long invoiceId = paymentDto.invoiceId();

        Invoice invoice = invoiceService.findInvoicesById(invoiceId);
        payment.setInvoice(invoice);

        return payment;
    }

    public List<Payment> findAllPayments() {

        return paymentRepo.findAll();
    }

    public Payment findPaymentById(Long paymentId) {

        return paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment with id " + paymentId));
    }
}

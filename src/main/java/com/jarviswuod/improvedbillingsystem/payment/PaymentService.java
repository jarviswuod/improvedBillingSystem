package com.jarviswuod.improvedbillingsystem.payment;

import com.jarviswuod.improvedbillingsystem.exception.BusinessRuleViolationException;
import com.jarviswuod.improvedbillingsystem.exception.ResourceNotFoundException;
import com.jarviswuod.improvedbillingsystem.invoice.Invoice;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceMapper;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceService;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final PaymentMapper paymentMapper;
    private final InvoiceService invoiceService;
    private final InvoiceMapper invoiceMapper;

    public void createPayment(PaymentDto paymentDto) {

        Payment payment = findByTransactionNumber(paymentDto.transactionNumber());

        if (payment != null)
            throw new BusinessRuleViolationException("Invalid Transaction Number");

        Payment paymentObj = paymentMapper.toPayment(paymentDto);

        Payment paymentMade = paymentRepo.save(paymentObj);
        invoiceStatusUpdate(paymentMade);
    }


    private Payment findByTransactionNumber(String transactionNumber) {
        return paymentRepo.findByTransactionNumber(transactionNumber);
    }


    private void invoiceStatusUpdate(Payment payment) {
        Invoice invoice = payment.getInvoice();

        invoice.setBalance(invoice.getBalance() - payment.getAmount());
        if (invoice.getBalance() < 0) {
            throw new BusinessRuleViolationException("Payment must not exceed the invoice amount");
        } else if (invoice.getBalance() == 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else if (invoice.getBalance() > 0) {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }
        invoiceService.updateInvoice(invoice);
    }

    public List<PaymentResponseDtoList> findAllPayments() {

        return paymentRepo.findAll()
                .stream()
                .map(paymentMapper::toPaymentResponseDtoList)
                .collect(Collectors.toList());
    }

    private Payment getPaymentById(Long paymentId){
        return paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment with id " + paymentId));
    }

    public PaymentResponseDto findPaymentById(Long paymentId) {

        return paymentMapper.toPaymentResponseDto(getPaymentById(paymentId));
    }
}

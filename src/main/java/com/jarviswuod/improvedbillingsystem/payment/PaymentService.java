package com.jarviswuod.improvedbillingsystem.payment;

import com.jarviswuod.improvedbillingsystem.exception.BusinessRuleViolationException;
import com.jarviswuod.improvedbillingsystem.exception.ResourceNotFoundException;
import com.jarviswuod.improvedbillingsystem.invoice.Invoice;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceService;
import com.jarviswuod.improvedbillingsystem.invoice.InvoiceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final PaymentMapper paymentMapper;
    private final InvoiceService invoiceService;


    public void createPayment(PaymentDto paymentDto) {

        Payment existingPayment = findByTransactionNumber(paymentDto.transactionNumber());

        if (existingPayment != null)
            throw new BusinessRuleViolationException("Invalid Transaction Number");

        Payment payment = paymentRepo.save(paymentMapper.toPayment(paymentDto));
        invoiceStatusUpdate(payment);

        log.info("Payment created successfully with paymentId {}", payment.getId());
    }


    private Payment findByTransactionNumber(String transactionNumber) {
        return paymentRepo.findByTransactionNumber(transactionNumber);
    }


    private void invoiceStatusUpdate(Payment payment) {
        Invoice invoice = payment.getInvoice();
        log.info("Updating invoice status {}, {}", invoice.getId(), invoice.getStatus());

        invoice.setBalance(invoice.getBalance() - payment.getAmount());
        if (invoice.getBalance() < 0)
            throw new BusinessRuleViolationException("Payment must not exceed the invoice amount");
        else if (invoice.getBalance() == 0)
            invoice.setStatus(InvoiceStatus.PAID);
        else if (invoice.getBalance() > 0)
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);

        invoiceService.updateInvoice(invoice);
        log.info("Invoice status updated {}, {}", invoice.getId(), invoice.getStatus());
    }


    @Transactional(readOnly = true)
    public List<PaymentResponseDtoList> findAllPayments() {

        return paymentRepo.findAll()
                .stream()
                .map(paymentMapper::toPaymentResponseDtoList)
                .collect(Collectors.toList());
    }


    private Payment getPaymentById(Long id) {
        return paymentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No payment with id " + id));
    }


    @Transactional(readOnly = true)
    public PaymentResponseDto findPaymentById(Long id) {

        PaymentResponseDto dto = paymentMapper.toPaymentResponseDto(getPaymentById(id));
        log.info("Payment retrieved paymentId {}", id);

        return dto;
    }


    public void updatePayment(UpdatePaymentDto dto, Long id) {
        Payment payment = getPaymentById(id);

        Payment updatedPayment = paymentMapper.toPayment(dto, payment);
        paymentRepo.save(updatedPayment);
        log.info("Payment updated successfully  paymentId {}", id);
    }


    public void deletePaymentById(Long id) {
        paymentRepo.deleteById(id);
        log.info("Payment successfully deleted paymentId {}", id);
    }
}

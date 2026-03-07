package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.exception.BusinessRuleViolationException;
import com.jarviswuod.improvedbillingsystem.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepo;
    private final InvoiceMapper invoiceMapper;

    public void save(InvoiceDto invoiceDto) {
        Invoice invoice = invoiceMapper.toInvoice(invoiceDto);

        invoiceRepo.save(invoice);
    }

    public List<InvoiceResponseDtoList> findAllInvoices() {
        return invoiceRepo.findAll()
                .stream()
                .map(invoiceMapper::toInvoiceResponseDtoList)
                .collect(Collectors.toList());
    }

    public InvoiceResponseDto findInvoicesById(Long invoiceId) {
        Invoice invoice = getInvoiceById(invoiceId);
        return invoiceMapper.toInvoiceResponseDto(invoice);
    }

    public Invoice getInvoiceById(Long invoiceId) {
        return invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new BusinessRuleViolationException("Invoice with id " + invoiceId + " does not exist."));
    }

    public void deleteInvoiceById(Long invoiceId) {
        Invoice invoice = getInvoiceById(invoiceId);
        List<Payment> payments = invoice.getPayments();

        if (payments.isEmpty())
            invoiceRepo.deleteById(invoiceId);
        else
            throw new BusinessRuleViolationException("An invoice with payments cannot be deleted");
    }

    public void updateInvoice(Invoice invoice) {
        invoiceRepo.save(invoice);
    }
}

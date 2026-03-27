package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.customer.CustomerService;
import com.jarviswuod.improvedbillingsystem.exception.BusinessRuleViolationException;
import com.jarviswuod.improvedbillingsystem.exception.ResourceNotFoundException;
import com.jarviswuod.improvedbillingsystem.payment.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepo;
    private final InvoiceMapper invoiceMapper;
    private final CustomerService customerService;


    public void createInvoice(InvoiceDto invoiceDto) {
        Invoice invoice = invoiceMapper.toInvoice(invoiceDto);

        log.info("Invoice created with invoiceId {}", invoice.getId());
        invoiceRepo.save(invoice);
    }


    @Transactional(readOnly = true)
    public List<InvoiceResponseDtoList> findAllInvoices() {
        return invoiceRepo.findAll()
                .stream()
                .map(invoiceMapper::toInvoiceResponseDtoList)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public InvoiceResponseDto findInvoicesById(Long id) {
        Invoice invoice = getInvoiceById(id);
        log.info("Invoice retrieved {}", invoice.getId());

        return invoiceMapper.toInvoiceResponseDto(invoice);
    }


    public Invoice getInvoiceById(Long id) {
        return invoiceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No invoice with id" + id));
    }


    public void deleteInvoiceById(Long id) {
        Invoice invoice = getInvoiceById(id);
        List<Payment> payments = invoice.getPayments();

        if (payments.isEmpty()) {
            invoiceRepo.deleteById(id);
            log.info("Invoice deleted with invoiceId {}", id);
        } else
            throw new BusinessRuleViolationException("An invoice with payments cannot be deleted");

        log.info("Invoice deleted successfully {}", id);
    }


    public void updateInvoice(Invoice invoice) {
        invoiceRepo.save(invoice);
        log.info("Invoice with invoiceId updated successfully {}", invoice.getId());
    }


    public void updateInvoice(InvoiceUpdateDto dto, Long id) {

        updateInvoice(invoiceMapper.toInvoice(dto, getInvoiceById(id)));
    }


    @Transactional(readOnly = true)
    public List<OverdueInvoiceDto> overDueInvoices(Long customerId, LocalDate startDate, LocalDate endDate
    ) {
        customerService.findActiveCustomerById(customerId);
        return invoiceRepo.overDueInvoices(customerId,startDate, endDate);
    }
}

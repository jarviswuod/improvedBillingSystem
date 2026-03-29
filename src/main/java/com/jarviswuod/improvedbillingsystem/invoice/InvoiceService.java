package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.customer.CustomerService;
import com.jarviswuod.improvedbillingsystem.exception.BusinessRuleViolationException;
import com.jarviswuod.improvedbillingsystem.exception.ResourceNotFoundException;
import com.jarviswuod.improvedbillingsystem.payment.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
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


    private void validateDates(LocalDate startDate, LocalDate endDate) {

        if (startDate != null && endDate != null && startDate.isAfter(endDate))
            throw new BusinessRuleViolationException("startDate must not be after endDate");

        if (endDate != null && endDate.isAfter(LocalDate.now()))
            throw new BusinessRuleViolationException("endDate must not be in the future");

    }


    @Transactional(readOnly = true)
    public List<OverdueInvoiceDto> getOverdueInvoices(
            Long customerId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        validateDates(startDate, endDate);
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        Instant startCreatedAt = startDate != null
                ? startDate.atStartOfDay(ZoneOffset.UTC).toInstant()
                : null;

        Instant endCreatedAt = endDate != null
                ? endDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant()
                : null;

        if (customerId != null)
            customerService.findActiveCustomerById(customerId);

        return invoiceRepo.findOverdueInvoices(customerId, today, startCreatedAt, endCreatedAt)
                .stream()
                .map(invoiceMapper::toOverdueInvoiceDto)
                .toList();
    }


    // -------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------


    public List<Invoice> fetchAllInvoicesWithAllPayments() {
        return invoiceRepo.fetchAllInvoicesWithAllPayments()
                .stream()
                .map((el) -> {
                    BigDecimal amountPaid = el.getPayments().stream()
                            .map(Payment::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    if (amountPaid.compareTo(BigDecimal.ZERO) == 0) {
                        el.setStatus(InvoiceStatus.PENDING);
                    } else if (amountPaid.compareTo(el.getAmount()) >= 0) {
                        el.setStatus(InvoiceStatus.PAID);
                    } else if (amountPaid.compareTo(el.getAmount()) <= 0) {
                        el.setStatus(InvoiceStatus.PARTIALLY_PAID);
                    }
                    updateInvoice(el);
                    return el;
                })
                .toList();
    }


    public Invoice fetchAnInvoicesWithAllPayments() {

        invoiceRepo.findAll()
                .stream()
                .map((el) -> {

                    Invoice invoice = invoiceRepo.fetchAnInvoicesWithAllPayments(el.getId())
                            .orElseThrow(() -> new RuntimeException("Invoice not found"));

                    BigDecimal amountPaid = invoice.getPayments().stream()
                            .map(Payment::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    if (amountPaid.compareTo(el.getAmount()) >= 0) {
                        el.setStatus(InvoiceStatus.PENDING);
                    } else if (amountPaid.compareTo(el.getAmount()) <= 0) {
                        el.setStatus(InvoiceStatus.PENDING);
                    }

                    updateInvoice(invoice);
                    return invoice;
                }).toList();

        return null;

    }


    public Invoice fetchSingleInvoicesWithSum() {
        invoiceRepo.findAll()
                .stream()
                .map((el) -> {
                    BigDecimal amountPaid = el.getPayments().stream()
                            .map(Payment::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    Object raw = invoiceRepo.fetchSingleInvoicesWithSum(el.getId())
                            .orElseThrow(() -> new RuntimeException("Invoice not found"));

                    Object[] result = (Object[]) raw;

                    Invoice inv = (Invoice) result[0];
                    BigDecimal totalPaid = (BigDecimal) result[1];

                    if (totalPaid.compareTo(inv.getAmount()) >= 0) {
                        inv.setStatus(InvoiceStatus.PENDING);
                    } else if (totalPaid.compareTo(inv.getAmount()) <= 0) {
                        inv.setStatus(InvoiceStatus.PENDING);
                    }

                    updateInvoice(inv);
                    return inv;
                }).toList();

        return null;
    }


    public Invoice fetchAllInvoicesWithSum() {
        invoiceRepo.fetchAllInvoicesWithSum()
                .stream()
                .map((el) -> {
//                    BigDecimal amountPaid = el.getPayments().stream()
//                            .map(Payment::getAmount)
//                            .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//                    Object raw = invoiceRepo.fetchSingleInvoicesWithSum(el.getId())
//                            .orElseThrow(() -> new RuntimeException("Invoice not found"));

                    Object[] result = (Object[]) el;

                    Invoice inv = (Invoice) result[0];
                    BigDecimal totalPaid = (BigDecimal) result[1];

                    if (totalPaid.compareTo(inv.getAmount()) >= 0) {
                        inv.setStatus(InvoiceStatus.PENDING);
                    } else if (totalPaid.compareTo(inv.getAmount()) <= 0) {
                        inv.setStatus(InvoiceStatus.PENDING);
                    }

                    updateInvoice(inv);
                    return inv;
                }).toList();

        return null;
    }
}
package com.jarviswuod.improvedbillingsystem.invoice;

import com.jarviswuod.improvedbillingsystem.customer.Customer;
import com.jarviswuod.improvedbillingsystem.customer.CustomerMapper;
import com.jarviswuod.improvedbillingsystem.customer.CustomerResponseDto;
import com.jarviswuod.improvedbillingsystem.customer.CustomerService;
import com.jarviswuod.improvedbillingsystem.exception.BusinessRuleViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    public Invoice save(InvoiceDto invoiceDto) {
        Invoice invoice = toInvoice(invoiceDto);

        return invoiceRepository.save(invoice);
    }

    private Invoice toInvoice(InvoiceDto invoiceDto) {

        Invoice invoice = new Invoice();
        invoice.setDueData(invoiceDto.dueDate());
        invoice.setAmount(invoiceDto.amount());
        invoice.setBalance(invoiceDto.amount());

        Long customerId = invoiceDto.customerId();
        CustomerResponseDto dto = customerService.findCustomerById(customerId);

        Customer customer = customerMapper.toCustomer(dto);
        invoice.setCustomer(customer);

        return invoice;
    }

    public List<Invoice> findAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice findInvoicesById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new BusinessRuleViolationException("Invoice with id " + invoiceId + " does not exist."));
    }

    public void deleteInvoiceById(Long invoiceId) {

        invoiceRepository.deleteById(invoiceId);
    }
}

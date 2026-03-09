package com.jarviswuod.improvedbillingsystem.customer;

import com.jarviswuod.improvedbillingsystem.exception.BusinessRuleViolationException;
import com.jarviswuod.improvedbillingsystem.exception.ResourceNotFoundException;
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
public class CustomerService {

    private final CustomerRepository customerRepo;
    private final CustomerMapper customerMapper;

    public CustomerResponseDto createCustomer(CustomerDto customerDto) {
        if (customerRepo.existsByEmailIncludingDeleted(customerDto.email()))
            throw new BusinessRuleViolationException("Account with email address " + customerDto.email() + " already exists");

        Customer customer = customerMapper.toCustomer(customerDto);
        Customer savedCustomer = customerRepo.save(customer);

        return customerMapper.toCustomerResponseDto(savedCustomer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDtoList> getAllActiveCustomers() {
        return customerRepo.findAll()
                .stream()
                .map(customerMapper::toCustomerResponseDtoList)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponseDto findCustomerById(Long customerId) {
        Customer customer = findActiveCustomerById(customerId);

        return customerMapper.toCustomerResponseDto(customer);
    }

    public Customer findActiveCustomerById(Long id) {
        return customerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Customer found with id: " + id));
    }


    public CustomerResponseDto updateCustomer(Long id, CustomerDto customerDto) {
        Customer customer = findActiveCustomerById(id);

        if (customerRepo.existsByEmailIncludingDeleted(customerDto.email()) &&
                !customer.getEmail().equals(customerDto.email())) {
            throw new BusinessRuleViolationException(
                    "Account with email address " + customerDto.email() + " already exists");
        }

        customerMapper.updateCustomer(customerDto, customer);
        Customer updatedCustomer = customerRepo.save(customer);

        return customerMapper.toCustomerResponseDto(updatedCustomer);
    }

    public void softDeleteCustomer(Long id) {
        Customer customer = findDeletedCustomerById(id);
        if (customer.isDeleted())
            throw new BusinessRuleViolationException("Customer already deleted with ID: " + id);

        customerRepo.deleteById(id);
        log.info("Customer soft deleted with ID: {}", id);
    }

    private Customer findDeletedCustomerById(Long id) {
//        Customer customer = findActiveCustomerById(id);
//        if (customer.isDeleted())
//            throw new BusinessRuleViolationException("Customer already deleted with ID: " + id);

        return customerRepo.findByIdInDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer already deleted with ID: " + id));
    }

    public void restoreCustomer(Long id) {
        Customer customer = findActiveCustomerById(id);

        if (!customer.isDeleted()) {
            throw new IllegalStateException("Customer is not deleted with ID: " + id);
        }

        customer.setDeleted(false);
        customer.setDeletedAt(null);

        customerRepo.save(customer);
        log.info("User restored with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDtoList> getAllDeletedCustomers() {
        return customerRepo.findAllDeleted()
                .stream()
                .map(customerMapper::toCustomerResponseDtoList)
                .collect(Collectors.toList());
    }

    public void permanentDeleteUser(Long id) {
        Customer customer = findActiveCustomerById(id);

        if (!customer.isDeleted())
            throw new IllegalStateException("User must be soft deleted first before permanent deletion");

        int deletedCount = customerRepo.permanentlyDeleteById(id);
        if (deletedCount > 0) {
            log.info("User permanently deleted with ID: {}", id);
        } else {
            throw new BusinessRuleViolationException("Customer not found for permanent deletion with ID: " + id);
        }
    }
}

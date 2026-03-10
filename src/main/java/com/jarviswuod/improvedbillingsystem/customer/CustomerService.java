package com.jarviswuod.improvedbillingsystem.customer;

import com.jarviswuod.improvedbillingsystem.exception.BusinessRuleViolationException;
import com.jarviswuod.improvedbillingsystem.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepo;
    private final CustomerMapper customerMapper;


    public CustomerResponseDto createCustomer(CustomerDto customerDto) {
        if (customerRepo.existsByEmailIncludingDeleted(customerDto.email())) {
            log.warn("Already Existing customer with email");
            throw new BusinessRuleViolationException("Account with email address " + customerDto.email() + " already exists");
        }
        Customer customer = customerMapper.toCustomer(customerDto);
        Customer savedCustomer = customerRepo.save(customer);

        log.info("Customer created successfully customerId: {}", savedCustomer.getId());

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
    public CustomerResponseDto findCustomerById(Long id) {
        Customer customer = findActiveCustomerById(id);

        log.info("Customer retrieved customerId: {}", id);
        return customerMapper.toCustomerResponseDto(customer);
    }


    @Transactional(readOnly = true)
    public Customer findActiveCustomerById(Long id) {
        return customerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Customer found with id: " + id));
    }


    public CustomerResponseDto updateCustomer(Long id, CustomerDto customerDto) {
        Customer customer = findActiveCustomerById(id);

        if (customerRepo.existsByEmailIncludingDeleted(customerDto.email())) {
            throw new BusinessRuleViolationException("Account with email address " + customerDto.email() + " already exists");
        }

        customerMapper.updateCustomer(customerDto, customer);
        Customer updatedCustomer = customerRepo.save(customer);

        log.info("Customer updated customerId: {}", updatedCustomer.getId());
        return customerMapper.toCustomerResponseDto(updatedCustomer);
    }


    public void softDeleteCustomer(Long id) {
        Optional<Customer> customerActive = customerRepo.findById(id);
        Optional<Customer> customerDeleted = customerRepo.findByIdInDeleted(id);

        if (customerDeleted.isPresent())
            throw new BusinessRuleViolationException("Customer already deleted with ID: " + id);

        else if (customerActive.isPresent())
            customerRepo.deleteById(id);

        else
            throw new BusinessRuleViolationException("No customer with with ID: " + id);

        log.info("Customer soft deleted with ID: {}", id);
    }


    public void restoreCustomer(Long id) {
        Optional<Customer> customer = customerRepo.findById(id);
        if (customer.isPresent()) {
            log.warn("Customer not deleted customerId {}", id);
            throw new BusinessRuleViolationException("Customer is not deleted with ID: " + id);
        }
        Customer restoreCustomer = customerRepo.findByIdInDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Customer with ID: " + id));

        restoreCustomer.setDeleted(false);
        restoreCustomer.setDeletedAt(null);

        customerRepo.save(restoreCustomer);

        log.info("Customer restored with customerId: {}", id);
    }


    @Transactional(readOnly = true)
    public List<CustomerResponseDtoList> getAllDeletedCustomers() {
        return customerRepo.findAllDeleted()
                .stream()
                .map(customerMapper::toCustomerResponseDtoList)
                .collect(Collectors.toList());
    }


    public void permanentDeleteCustomer(Long id) {
        Optional<Customer> customer = customerRepo.findById(id);

        if (customer.isPresent())
            throw new BusinessRuleViolationException("Customer must be soft deleted first before permanent deletion");

        int deletedCount = customerRepo.permanentlyDeleteById(id);
        if (deletedCount > 0) {
            log.info("Customer permanently deleted with ID: {}", id);
        } else {
            throw new BusinessRuleViolationException("Customer not found for permanent deletion with ID: " + id);
        }
    }
}

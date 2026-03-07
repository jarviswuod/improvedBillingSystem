package com.jarviswuod.improvedbillingsystem.customer;

import com.jarviswuod.improvedbillingsystem.exception.BusinessRuleViolationException;
import com.jarviswuod.improvedbillingsystem.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepo;
    private final CustomerMapper customerMapper;

    public CustomerResponseDto createCustomer(CustomerDto customerDto) {
        if (customerRepo.existsByEmail(customerDto.email()))
            throw new BusinessRuleViolationException("Account with email address " + customerDto.email() + " already exists");

        Customer customer = customerMapper.toCustomer(customerDto);
        Customer savedCustomer = customerRepo.save(customer);

        return customerMapper.toCustomerResponseDto(savedCustomer);
    }

    public List<CustomerResponseDtoList> findAllCustomers() {
        return customerRepo.findAll()
                .stream()
                .map(customerMapper::toCustomerResponseDtoList)
                .collect(Collectors.toList());
    }

    public CustomerResponseDto findCustomerById(Long customerId) {

        Customer customer = getCustomerEntityById(customerId);
        return customerMapper.toCustomerResponseDto(customer);
    }

    public Customer getCustomerEntityById(Long customerId) {
        return customerRepo.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("No Customer found with id: " + customerId));
    }

    public CustomerResponseDto updateCustomer(Long customerId, CustomerDto customerDto) {

        Customer customer = getCustomerEntityById(customerId);

        if (customerRepo.existsByEmail(customerDto.email()) &&
                !customer.getEmail().equals(customerDto.email())) {
            throw new BusinessRuleViolationException(
                    "Account with email address " + customerDto.email() + " already exists");
        }

        customerMapper.updateCustomer(customerDto, customer);

        Customer updatedCustomer = customerRepo.save(customer);

        return customerMapper.toCustomerResponseDto(updatedCustomer);
    }

    /*
    public CustomerResponseDto updateCustomer(Long customerId, CustomerDto customerDto) {
        if (customerRepo.existsByEmail(customerDto.email()))
            throw new BusinessRuleViolationException("Account with email address " + customerDto.email() + " already exists");

        findCustomerById(customerId);
        Customer customer = customerMapper.toCustomer(customerDto);
        Customer updatedCustomer = customerRepo.save(customer);

        return customerMapper.toCustomerResponseDto(updatedCustomer);
    }
     */

    /*
    public CustomerResponseDto updateCustomer(Long customerId, CustomerDto customerDto) {

        Customer customer = findCustomerById(customerId);

        if (customerRepo.existsByEmail(customerDto.email()) &&
                !customer.getEmail().equals(customerDto.email())) {
            throw new BusinessRuleViolationException(
                    "Account with email address " + customerDto.email() + " already exists");
        }

        customerMapper.updateCustomer(customerDto, customer);

        Customer updatedCustomer = customerRepo.save(customer);

        return customerMapper.toCustomerResponseDto(updatedCustomer);
    }
    */

    public void deleteCustomer(Long customerId) {

        findCustomerById(customerId);
        customerRepo.deleteById(customerId);
    }
}

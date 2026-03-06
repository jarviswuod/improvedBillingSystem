package com.jarviswuod.improvedbillingsystem.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public CustomerResponseDto createCustomer(
            @Valid @RequestBody CustomerDto customerDto
    ) {
        return customerService.createCustomer(customerDto);
    }

    @GetMapping
    public List<CustomerResponseDtoList> findAllCustomers() {
        return customerService.findAllCustomers();
    }

    @GetMapping("/{customer-id}")
    public CustomerResponseDto findAllCustomers(
            @PathVariable("customer-id") Long customerId
    ) {
        return customerService.findCustomerById(customerId);
    }

    @PutMapping("/{customer-id}")
    public CustomerResponseDto updateCustomer(
            @PathVariable("customer-id") Long customerId,
            @Valid @RequestBody CustomerDto customerDto
    ) {
        return customerService.updateCustomer(customerId, customerDto);
    }

    @DeleteMapping("/{customer-id}")
    public void deleteCustomer(
            @PathVariable("customer-id") Long customerId
    ) {
        customerService.deleteCustomer(customerId);
    }
}

package com.jarviswuod.improvedbillingsystem.customer;

import org.springframework.stereotype.Service;

@Service
public class CustomerMapper {

    public Customer toCustomer(CustomerDto customerDto) {

        Customer customer = new Customer();
        customer.setName(customerDto.name());
        customer.setEmail(customerDto.email());
        customer.setPhone(customerDto.phone());

        return customer;
    }

    public Customer toCustomer(CustomerResponseDto customerResponseDto) {

        Customer customer = new Customer();
        customer.setName(customerResponseDto.name());
        customer.setEmail(customerResponseDto.email());
        customer.setPhone(customerResponseDto.phone());

        return customer;
    }

    public CustomerResponseDto toCustomerResponseDto(Customer customer) {

        return new CustomerResponseDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }

    public CustomerResponseDtoList toCustomerResponseDtoList(Customer customer) {
        return new CustomerResponseDtoList(
                customer.getId(),
                customer.getName()
        );
    }

    public void updateCustomer(CustomerDto dto, Customer customer) {
        customer.setName(dto.name());
        customer.setEmail(dto.email());
        customer.setPhone(dto.phone());
    }
}

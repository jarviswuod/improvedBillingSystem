package com.jarviswuod.improvedbillingsystem.customer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerMapperTest {

    private final CustomerMapper customerMapper = new CustomerMapper();


    @Test
    void toCustomerShouldMapRequestFields() {
        // Given
        CustomerDto dto = new CustomerDto("Jarvis", "jarvis@example.com", "+254712345678");

        // When
        Customer customer = customerMapper.toCustomer(dto);

        // Then
        assertEquals(dto.name(), customer.getName());
        assertEquals(dto.email(), customer.getEmail());
        assertEquals(dto.phone(), customer.getPhone());
    }


    @Test
    void toCustomerResponseDtoShouldMapEntityFields() {
        // Given
        Customer customer = Customer.builder()
                .name("Jarvis")
                .email("jarvis@example.com")
                .phone("+254712345678")
                .build();
        customer.setId(1L);

        // When
        CustomerResponseDto responseDto = customerMapper.toCustomerResponseDto(customer);

        // Then
        assertEquals(customer.getId(), responseDto.id());
        assertEquals(customer.getName(), responseDto.name());
        assertEquals(customer.getEmail(), responseDto.email());
        assertEquals(customer.getPhone(), responseDto.phone());
    }


    @Test
    void updateCustomerShouldMutateExistingCustomer() {
        // Given
        Customer customer = Customer.builder()
                .name("Old Name")
                .email("old@example.com")
                .phone("+254700000000")
                .build();
        CustomerDto dto = new CustomerDto("New Name", "new@example.com", "+254711111111");

        // When
        customerMapper.updateCustomer(dto, customer);

        // Then
        assertEquals(dto.name(), customer.getName());
        assertEquals(dto.email(), customer.getEmail());
        assertEquals(dto.phone(), customer.getPhone());
    }
}

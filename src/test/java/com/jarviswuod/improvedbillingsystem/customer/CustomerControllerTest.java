package com.jarviswuod.improvedbillingsystem.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerControllerTest {

    private CustomerController customerController;

    @Mock
    private CustomerService customerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        customerController = new CustomerController(customerService);
    }

    @Test
    void createCustomerShouldReturnCreatedCustomer() {
        // Given
        CustomerDto dto = new CustomerDto("Jarvis", "jarvis@example.com", "+254712345678");
        CustomerResponseDto expectedResponse =
                new CustomerResponseDto(1L, dto.name(), dto.email(), dto.phone());

        when(customerService.createCustomer(dto)).thenReturn(expectedResponse);

        // When
        ResponseEntity<CustomerResponseDto> response = customerController.createCustomer(dto);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(customerService).createCustomer(dto);
    }

    @Test
    void getAllActiveCustomersShouldReturnCustomers() {
        // Given
        List<CustomerResponseDtoList> customers = List.of(
                new CustomerResponseDtoList(1L, "Jarvis"),
                new CustomerResponseDtoList(2L, "Ali")
        );
        when(customerService.getAllActiveCustomers()).thenReturn(customers);

        // When
        ResponseEntity<List<CustomerResponseDtoList>> response = customerController.getAllActiveCustomers();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customers, response.getBody());
    }

    @Test
    void getCustomerByIdShouldReturnCustomer() {
        // Given
        long id = 1L;
        CustomerResponseDto expectedResponse =
                new CustomerResponseDto(id, "Jarvis", "jarvis@example.com", "+254712345678");
        when(customerService.findCustomerById(id)).thenReturn(expectedResponse);

        // When
        ResponseEntity<CustomerResponseDto> response = customerController.getCustomerById(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void updateCustomerShouldReturnUpdatedCustomer() {
        // Given
        long id = 1L;
        CustomerDto dto = new CustomerDto("New Name", "new@example.com", "+254798765432");
        CustomerResponseDto expectedResponse =
                new CustomerResponseDto(id, dto.name(), dto.email(), dto.phone());
        when(customerService.updateCustomer(id, dto)).thenReturn(expectedResponse);

        // When
        ResponseEntity<CustomerResponseDto> response = customerController.updateCustomer(id, dto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void softDeleteCustomerShouldReturnNoContent() {
        // Given
        long id = 1L;
        doNothing().when(customerService).softDeleteCustomer(id);

        // When
        ResponseEntity<Void> response = customerController.softDeleteCustomer(id);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(customerService).softDeleteCustomer(id);
    }

    @Test
    void restoreCustomerShouldReturnOk() {
        // Given
        long id = 1L;
        doNothing().when(customerService).restoreCustomer(id);

        // When
        ResponseEntity<Void> response = customerController.restoreCustomer(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(customerService).restoreCustomer(id);
    }

    @Test
    void getAllDeletedCustomersShouldReturnDeletedCustomers() {
        // Given
        List<CustomerResponseDtoList> customers = List.of(new CustomerResponseDtoList(1L, "Jarvis"));
        when(customerService.getAllDeletedCustomers()).thenReturn(customers);

        // When
        ResponseEntity<List<CustomerResponseDtoList>> response = customerController.getAllDeletedCustomers();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customers, response.getBody());
    }

    @Test
    void permanentDeleteCustomerShouldReturnNoContent() {
        // Given
        long id = 1L;
        doNothing().when(customerService).permanentDeleteCustomer(id);

        // When
        ResponseEntity<Void> response = customerController.permanentDeleteCustomer(id);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(customerService).permanentDeleteCustomer(id);
    }
}

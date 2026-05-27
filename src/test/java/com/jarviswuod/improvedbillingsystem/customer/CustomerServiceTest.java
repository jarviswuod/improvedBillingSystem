package com.jarviswuod.improvedbillingsystem.customer;

import com.jarviswuod.improvedbillingsystem.exception.BusinessRuleViolationException;
import com.jarviswuod.improvedbillingsystem.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private CustomerMapper customerMapper;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createCustomerShouldSaveAndReturnResponse() {
        // Given
        CustomerDto dto = new CustomerDto("Jarvis", "jarvis@example.com", "+254712345678");
        Customer customer = Customer.builder()
                .name(dto.name())
                .email(dto.email())
                .phone(dto.phone())
                .build();
        Customer savedCustomer = Customer.builder()
                .name(dto.name())
                .email(dto.email())
                .phone(dto.phone())
                .build();
        savedCustomer.setId(1L);
        CustomerResponseDto expectedResponse =
                new CustomerResponseDto(1L, dto.name(), dto.email(), dto.phone());


        when(customerRepo.existsByEmailIncludingDeleted(dto.email()))
                .thenReturn(false);
        when(customerMapper.toCustomer(dto))
                .thenReturn(customer);
        when(customerRepo.save(customer))
                .thenReturn(savedCustomer);
        when(customerMapper.toCustomerResponseDto(savedCustomer))
                .thenReturn(expectedResponse);

        // When
        CustomerResponseDto response = customerService.createCustomer(dto);

        // Then
        assertEquals(expectedResponse, response);
        verify(customerRepo).save(customerCaptor.capture());
        Customer capturedCustomer = customerCaptor.getValue();
        assertEquals(dto.name(), capturedCustomer.getName());
        assertEquals(dto.email(), capturedCustomer.getEmail());
        assertEquals(dto.phone(), capturedCustomer.getPhone());
    }


    @Test
    void createCustomerShouldThrowWhenEmailAlreadyExists() {
        // Given
        CustomerDto dto = new CustomerDto("Jarvis", "jarvis@example.com", "+254712345678");
        when(customerRepo.existsByEmailIncludingDeleted(dto.email()))
                .thenReturn(true);

        // When & Then
        BusinessRuleViolationException exp = assertThrows(
                BusinessRuleViolationException.class,
                () -> customerService.createCustomer(dto)
        );

        assertEquals("Account with email address jarvis@example.com already exists", exp.getMessage());
        verify(customerRepo, never()).save(any());
        verifyNoInteractions(customerMapper);
    }


    @Test
    void getAllActiveCustomersShouldReturnMappedCustomers() {
        // Given
        Customer first = Customer.builder()
                .name("Jarvis")
                .email("jarvis@example.com")
                .build();
        first.setId(1L);
        Customer second = Customer.builder()
                .name("Ali")
                .email("ali@example.com")
                .build();
        second.setId(2L);

        when(customerRepo.findAll())
                .thenReturn(List.of(first, second));
        when(customerMapper.toCustomerResponseDtoList(first))
                .thenReturn(new CustomerResponseDtoList(1L, "Jarvis"));
        when(customerMapper.toCustomerResponseDtoList(second))
                .thenReturn(new CustomerResponseDtoList(2L, "Ali"));

        // When
        List<CustomerResponseDtoList> response = customerService.getAllActiveCustomers();

        // Then
        assertThat(response)
                .hasSize(2);
        assertEquals("Jarvis", response.get(0).name());
        assertEquals("Ali", response.get(1).name());
    }


    @Test
    void findCustomerByIdShouldThrowWhenCustomerDoesNotExist() {
        // Given
        long id = 99L;
        when(customerRepo.findById(id))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exp = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.findCustomerById(id)
        );
        assertEquals("No Customer found with id: 99", exp.getMessage());
        verify(customerMapper, never()).toCustomerResponseDto(any());
    }


    @Test
    void findCustomerByIdShouldReturnMappedCustomer() {
        // Given
        long id = 1L;
        Customer customer = Customer.builder()
                .name("Jarvis")
                .email("jarvis@example.com")
                .phone("+254712345678")
                .build();
        customer.setId(id);
        CustomerResponseDto expectedResponse =
                new CustomerResponseDto(id, customer.getName(), customer.getEmail(), customer.getPhone());

        when(customerRepo.findById(id))
                .thenReturn(Optional.of(customer));
        when(customerMapper.toCustomerResponseDto(customer))
                .thenReturn(expectedResponse);

        // When
        CustomerResponseDto response = customerService.findCustomerById(id);

        // Then
        assertEquals(expectedResponse, response);
        verify(customerMapper).toCustomerResponseDto(customer);
    }


    @Test
    void updateCustomerShouldSaveMappedChanges() {
        // Given
        long id = 1L;
        CustomerDto dto = new CustomerDto("New Name", "new@example.com", "+254798765432");
        Customer existingCustomer = Customer.builder()
                .name("Old Name")
                .email("old@example.com")
                .phone("+254712345678")
                .build();
        existingCustomer.setId(id);
        CustomerResponseDto expectedResponse =
                new CustomerResponseDto(id, dto.name(), dto.email(), dto.phone());

        when(customerRepo.findById(id))
                .thenReturn(Optional.of(existingCustomer));
        when(customerRepo.existsByEmailIncludingDeleted(dto.email()))
                .thenReturn(false);
        when(customerRepo.save(existingCustomer))
                .thenReturn(existingCustomer);
        when(customerMapper.toCustomerResponseDto(existingCustomer))
                .thenReturn(expectedResponse);

        // When
        CustomerResponseDto response = customerService.updateCustomer(id, dto);

        // Then
        assertEquals(expectedResponse, response);
        verify(customerMapper).updateCustomer(dto, existingCustomer);
        verify(customerRepo).save(existingCustomer);
    }


    @Test
    void updateCustomerShouldThrowWhenEmailAlreadyExists() {
        // Given
        long id = 1L;
        CustomerDto dto = new CustomerDto("New Name", "existing@example.com", "+254798765432");
        Customer existingCustomer = Customer.builder()
                .name("Old Name")
                .email("old@example.com")
                .phone("+254712345678")
                .build();

        when(customerRepo.findById(id))
                .thenReturn(Optional.of(existingCustomer));
        when(customerRepo.existsByEmailIncludingDeleted(dto.email()))
                .thenReturn(true);

        // When & Then
        BusinessRuleViolationException exp = assertThrows(
                BusinessRuleViolationException.class,
                () -> customerService.updateCustomer(id, dto)
        );

        assertEquals("Account with email address existing@example.com already exists", exp.getMessage());
        verify(customerMapper, never()).updateCustomer(any(), any());
        verify(customerRepo, never()).save(any());
    }


    @Test
    void softDeleteCustomerShouldDeleteActiveCustomer() {
        // Given
        long id = 1L;
        Customer customer = Customer.builder()
                .name("Jarvis")
                .email("jarvis@example.com")
                .build();

        when(customerRepo.findById(id))
                .thenReturn(Optional.of(customer));
        when(customerRepo.findByIdInDeleted(id))
                .thenReturn(Optional.empty());
        doNothing().when(customerRepo).deleteById(id);

        // When
        customerService.softDeleteCustomer(id);

        // Then
        verify(customerRepo).deleteById(id);
    }


    @Test
    void softDeleteCustomerShouldThrowWhenCustomerAlreadyDeleted() {
        // Given
        long id = 1L;
        Customer deletedCustomer = Customer.builder()
                .name("Jarvis")
                .email("jarvis@example.com")
                .build();

        when(customerRepo.findById(id))
                .thenReturn(Optional.empty());
        when(customerRepo.findByIdInDeleted(id))
                .thenReturn(Optional.of(deletedCustomer));

        // When & Then
        BusinessRuleViolationException exp = assertThrows(
                BusinessRuleViolationException.class,
                () -> customerService.softDeleteCustomer(id)
        );

        assertEquals("Customer already deleted with ID: 1", exp.getMessage());
        verify(customerRepo, never()).deleteById(id);
    }


    @Test
    void softDeleteCustomerShouldThrowWhenCustomerDoesNotExist() {
        // Given
        long id = 1L;
        when(customerRepo.findById(id))
                .thenReturn(Optional.empty());
        when(customerRepo.findByIdInDeleted(id))
                .thenReturn(Optional.empty());

        // When & Then
        BusinessRuleViolationException exp = assertThrows(
                BusinessRuleViolationException.class,
                () -> customerService.softDeleteCustomer(id)
        );

        assertEquals("No customer with with ID: 1", exp.getMessage());
        verify(customerRepo, never()).deleteById(id);
    }


    @Test
    void restoreCustomerShouldSaveDeletedCustomerAsActive() {
        // Given
        long id = 1L;
        Customer deletedCustomer = Customer.builder()
                .name("Jarvis")
                .email("jarvis@example.com")
                .build();
        deletedCustomer.setId(id);
        deletedCustomer.setDeleted(true);

        when(customerRepo.findById(id))
                .thenReturn(Optional.empty());
        when(customerRepo.findByIdInDeleted(id))
                .thenReturn(Optional.of(deletedCustomer));
        when(customerRepo.save(deletedCustomer))
                .thenReturn(deletedCustomer);

        // When
        customerService.restoreCustomer(id);

        // Then
        verify(customerRepo).save(customerCaptor.capture());
        Customer capturedCustomer = customerCaptor.getValue();
        assertThat(capturedCustomer.isDeleted())
                .isFalse();
        assertThat(capturedCustomer.getDeletedAt())
                .isNull();
    }


    @Test
    void restoreCustomerShouldThrowWhenCustomerIsAlreadyActive() {
        // Given
        long id = 1L;
        Customer activeCustomer = Customer.builder()
                .name("Jarvis")
                .email("jarvis@example.com")
                .build();
        when(customerRepo.findById(id))
                .thenReturn(Optional.of(activeCustomer));

        // When & Then
        BusinessRuleViolationException exp = assertThrows(
                BusinessRuleViolationException.class,
                () -> customerService.restoreCustomer(id)
        );

        assertEquals("Customer is not deleted with ID: 1", exp.getMessage());
        verify(customerRepo, never()).findByIdInDeleted(id);
        verify(customerRepo, never()).save(any());
    }


    @Test
    void restoreCustomerShouldThrowWhenDeletedCustomerDoesNotExist() {
        // Given
        long id = 1L;
        when(customerRepo.findById(id))
                .thenReturn(Optional.empty());
        when(customerRepo.findByIdInDeleted(id))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exp = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.restoreCustomer(id)
        );

        assertEquals("No Customer with ID: 1", exp.getMessage());
        verify(customerRepo, never()).save(any());
    }


    @Test
    void getAllDeletedCustomersShouldReturnMappedDeletedCustomers() {
        // Given
        Customer deletedCustomer = Customer.builder()
                .name("Jarvis")
                .email("jarvis@example.com")
                .build();
        deletedCustomer.setId(1L);
        CustomerResponseDtoList expectedResponse = new CustomerResponseDtoList(1L, "Jarvis");

        when(customerRepo.findAllDeleted())
                .thenReturn(List.of(deletedCustomer));
        when(customerMapper.toCustomerResponseDtoList(deletedCustomer))
                .thenReturn(expectedResponse);

        // When
        List<CustomerResponseDtoList> response = customerService.getAllDeletedCustomers();

        // Then
        assertThat(response).containsExactly(expectedResponse);
    }


    @Test
    void permanentDeleteCustomerShouldDeleteOnlySoftDeletedCustomer() {
        // Given
        long id = 1L;
        when(customerRepo.findById(id))
                .thenReturn(Optional.empty());
        when(customerRepo.permanentlyDeleteById(id))
                .thenReturn(1);

        // When
        customerService.permanentDeleteCustomer(id);

        // Then
        verify(customerRepo).permanentlyDeleteById(id);
    }


    @Test
    void permanentDeleteCustomerShouldThrowWhenCustomerIsActive() {
        // Given
        long id = 1L;
        Customer activeCustomer = Customer.builder().name("Jarvis").email("jarvis@example.com").build();
        when(customerRepo.findById(id))
                .thenReturn(Optional.of(activeCustomer));

        // When & Then
        BusinessRuleViolationException exp = assertThrows(
                BusinessRuleViolationException.class,
                () -> customerService.permanentDeleteCustomer(id)
        );

        assertEquals("Customer must be soft deleted first before permanent deletion", exp.getMessage());
        verify(customerRepo, never()).permanentlyDeleteById(id);
    }


    @Test
    void permanentDeleteCustomerShouldThrowWhenDeletedCustomerDoesNotExist() {
        // Given
        long id = 1L;
        when(customerRepo.findById(id))
                .thenReturn(Optional.empty());
        when(customerRepo.permanentlyDeleteById(id))
                .thenReturn(0);

        // When & Then
        BusinessRuleViolationException exp = assertThrows(
                BusinessRuleViolationException.class,
                () -> customerService.permanentDeleteCustomer(id)
        );

        assertEquals("Customer not found for permanent deletion with ID: 1", exp.getMessage());
    }
}

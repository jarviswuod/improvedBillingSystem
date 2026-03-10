package com.jarviswuod.improvedbillingsystem.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(
            @Valid @RequestBody CustomerDto customerDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(customerDto));
    }


    @GetMapping
    public ResponseEntity<List<CustomerResponseDtoList>> getAllActiveCustomers() {
        return ResponseEntity.ok(customerService.getAllActiveCustomers());
    }


    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getCustomerById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(customerService.findCustomerById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDto customerDto
    ) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerDto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteCustomer(
            @PathVariable Long id
    ) {
        customerService.softDeleteCustomer(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restoreCustomer(@PathVariable Long id) {
        customerService.restoreCustomer(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/deleted")
    public ResponseEntity<List<CustomerResponseDtoList>> getAllDeletedCustomers() {
        return ResponseEntity.ok(customerService.getAllDeletedCustomers());
    }


    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentDeleteCustomer(@PathVariable Long id) {
        customerService.permanentDeleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}

package com.jarviswuod.improvedbillingsystem.customer;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management operations (soft delete + restore included)")
@SecurityRequirement(name = "bearer-jwt")
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping
    @Operation(
            summary = "Create a customer",
            description = "Creates a new active customer. Email must be unique."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created"),
            @ApiResponse(responseCode = "409", description = "Customer with this email already exists"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<CustomerResponseDto> createCustomer(
            @Valid @RequestBody CustomerDto customerDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(customerDto));
    }


    @GetMapping
    @Operation(summary = "List active customers", description = "Returns all customers that are not soft-deleted.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customers retrieved"),
    })
    public ResponseEntity<List<CustomerResponseDtoList>> getAllActiveCustomers() {
        return ResponseEntity.ok(customerService.getAllActiveCustomers());
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get customer by id", description = "Retrieves an active customer by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer retrieved"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerResponseDto> getCustomerById(
            @Parameter(description = "Customer id", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(customerService.findCustomerById(id));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Updates an active customer's fields (name, email, phone).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "409", description = "Customer with this email already exists"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @Parameter(description = "Customer id", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CustomerDto customerDto
    ) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerDto));
    }


    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete customer",
            description = "Marks a customer as deleted (records are not permanently removed)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer soft-deleted"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> softDeleteCustomer(
            @Parameter(description = "Customer id", example = "1")
            @PathVariable Long id
    ) {
        customerService.softDeleteCustomer(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/restore")
    @Operation(
            summary = "Restore soft-deleted customer",
            description = "Restores a customer that was previously soft-deleted."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer restored"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> restoreCustomer(@PathVariable Long id) {
        customerService.restoreCustomer(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/deleted")
    @Operation(summary = "List deleted customers", description = "Returns customers that are soft-deleted.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted customers retrieved")
    })
    public ResponseEntity<List<CustomerResponseDtoList>> getAllDeletedCustomers() {
        return ResponseEntity.ok(customerService.getAllDeletedCustomers());
    }


    @DeleteMapping("/{id}/permanent")
    @Operation(
            summary = "Permanently delete customer",
            description = "Permanently removes a customer from the database. A customer must be soft-deleted first."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer permanently deleted"),
            @ApiResponse(responseCode = "400", description = "Customer must be soft-deleted first or cannot be deleted"),
    })
    public ResponseEntity<Void> permanentDeleteCustomer(@PathVariable Long id) {
        customerService.permanentDeleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}

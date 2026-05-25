package com.jarviswuod.improvedbillingsystem.customer;

import com.jarviswuod.improvedbillingsystem.AbstractTestContainerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class CustomerIntTest extends AbstractTestContainerTest {

    private static final String API_PATH = "/api/customers";

    @Autowired
    private TestRestTemplate restTemplate;

    private CustomerDto uniqueCustomerRequest() {
        return new CustomerDto(
                "Test User",
                "user-" + UUID.randomUUID() + "@gmail.com",
                "+254712345678"
        );
    }

    private Long createAndFetchId(CustomerDto request) {
        ResponseEntity<CustomerResponseDto> create = restTemplate.exchange(
                API_PATH, POST, new HttpEntity<>(request), CustomerResponseDto.class);

        assertThat(create.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(create.getBody()).isNotNull();

        return Objects.requireNonNull(create.getBody()).id();
    }

    @Test
    void shouldCreateCustomer() {
        // Given
        CustomerDto request = uniqueCustomerRequest();

        // When
        ResponseEntity<CustomerResponseDto> response = restTemplate.exchange(
                API_PATH, POST, new HttpEntity<>(request), CustomerResponseDto.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().email()).isEqualTo(request.email());
    }

    @Test
    void shouldReturnAllCustomers() {
        // Given
        createAndFetchId(uniqueCustomerRequest());
        createAndFetchId(uniqueCustomerRequest());

        // When
        ResponseEntity<List<CustomerResponseDtoList>> response = restTemplate.exchange(
                API_PATH, GET, null, new ParameterizedTypeReference<>() {
                });

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldFetchCustomerById() {
        // Given
        CustomerDto request = uniqueCustomerRequest();
        Long id = createAndFetchId(request);

        // When
        ResponseEntity<CustomerResponseDto> response = restTemplate.exchange(
                API_PATH + "/" + id, GET, null, CustomerResponseDto.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().email()).isEqualTo(request.email());
    }

    @Test
    void shouldUpdateCustomer() {
        // Given
        Long id = createAndFetchId(uniqueCustomerRequest());
        CustomerDto updateRequest = new CustomerDto(
                "Updated User",
                "updated-" + UUID.randomUUID() + "@gmail.com",
                "+254798765432"
        );

        // When
        ResponseEntity<CustomerResponseDto> updateResponse = restTemplate.exchange(
                API_PATH + "/" + id, PUT, new HttpEntity<>(updateRequest), CustomerResponseDto.class);

        // Then
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().email()).isEqualTo(updateRequest.email());
        assertThat(updateResponse.getBody().name()).isEqualTo(updateRequest.name());
    }

    @Test
    void shouldSoftDeleteCustomer() {
        // Given
        Long id = createAndFetchId(uniqueCustomerRequest());

        // When
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                API_PATH + "/" + id, DELETE, null, Void.class);

        // Then
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> fetched = restTemplate.exchange(
                API_PATH + "/" + id, GET, null, String.class);

        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldRestoreCustomer() {
        // Given
        Long id = createAndFetchId(uniqueCustomerRequest());
        restTemplate.exchange(API_PATH + "/" + id, DELETE, null, Void.class);

        // When
        ResponseEntity<Void> restoreResponse = restTemplate.exchange(
                API_PATH + "/" + id + "/restore", POST, null, Void.class);

        // Then
        assertThat(restoreResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<CustomerResponseDto> fetched = restTemplate.exchange(
                API_PATH + "/" + id, GET, null, CustomerResponseDto.class);

        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetched.getBody()).isNotNull();
        assertThat(fetched.getBody().id()).isEqualTo(id);
    }

    @Test
    void shouldPermanentlyDeleteSoftDeletedCustomer() {
        // Given
        Long id = createAndFetchId(uniqueCustomerRequest());
        restTemplate.exchange(API_PATH + "/" + id, DELETE, null, Void.class);

        // When
        ResponseEntity<Void> permanentDeleteResponse = restTemplate.exchange(
                API_PATH + "/" + id + "/permanent", DELETE, null, Void.class);

        // Then
        assertThat(permanentDeleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> fetched = restTemplate.exchange(
                API_PATH + "/" + id, GET, null, String.class);

        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn404WhenCustomerNotFound() {
        // Given
        long nonExistentId = Long.MAX_VALUE;

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                API_PATH + "/" + nonExistentId, GET, null, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturn400WhenCreatingCustomerWithInvalidData() {
        // Given
        CustomerDto badRequest = new CustomerDto("", "", "");

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                API_PATH, POST, new HttpEntity<>(badRequest), String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn409WhenCreatingDuplicateEmail() {
        // Given
        CustomerDto request = uniqueCustomerRequest();
        createAndFetchId(request);

        // When
        ResponseEntity<String> duplicate = restTemplate.exchange(
                API_PATH, POST, new HttpEntity<>(request), String.class);

        // Then
        assertThat(duplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}

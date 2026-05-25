package com.jarviswuod.improvedbillingsystem.customer;

import com.jarviswuod.improvedbillingsystem.AbstractTestContainerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class CustomerRepositoryTest extends AbstractTestContainerTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void existsByEmailIncludingDeletedShouldFindActiveAndDeletedCustomers() {
        // Given
        Customer activeCustomer = customer("Active User", "active@example.com", false);
        Customer deletedCustomer = customer("Deleted User", "deleted@example.com", true);
        customerRepository.saveAllAndFlush(List.of(activeCustomer, deletedCustomer));

        // When & Then
        assertThat(customerRepository.existsByEmailIncludingDeleted(activeCustomer.getEmail())).isTrue();
        assertThat(customerRepository.existsByEmailIncludingDeleted(deletedCustomer.getEmail())).isTrue();
        assertThat(customerRepository.existsByEmailIncludingDeleted("missing@example.com")).isFalse();
    }

    @Test
    void findByIdInDeletedShouldReturnOnlyDeletedCustomer() {
        // Given
        Customer deletedCustomer = customer("Deleted User", "deleted-id@example.com", true);
        Customer savedCustomer = customerRepository.saveAndFlush(deletedCustomer);

        // When
        Optional<Customer> response = customerRepository.findByIdInDeleted(savedCustomer.getId());

        // Then
        assertThat(response).isPresent();
        assertThat(response.get().getEmail()).isEqualTo(savedCustomer.getEmail());
    }

    @Test
    void findAllDeletedShouldReturnOnlyDeletedCustomers() {
        // Given
        customerRepository.saveAndFlush(customer("Active User", "active-list@example.com", false));
        Customer deletedCustomer = customerRepository.saveAndFlush(
                customer("Deleted User", "deleted-list@example.com", true)
        );

        // When
        List<Customer> deletedCustomers = customerRepository.findAllDeleted();

        // Then
        assertThat(deletedCustomers)
                .extracting(Customer::getEmail)
                .contains(deletedCustomer.getEmail())
                .doesNotContain("active-list@example.com");
    }

    @Test
    void permanentlyDeleteByIdShouldDeleteOnlySoftDeletedCustomer() {
        // Given
        Customer deletedCustomer = customerRepository.saveAndFlush(
                customer("Deleted User", "permanent@example.com", true)
        );

        // When
        int deletedCount = customerRepository.permanentlyDeleteById(deletedCustomer.getId());
        customerRepository.flush();

        // Then
        assertThat(deletedCount).isEqualTo(1);
        assertThat(customerRepository.findByIdInDeleted(deletedCustomer.getId())).isNotPresent();
    }

    private Customer customer(String name, String email, boolean deleted) {
        Customer customer = Customer.builder()
                .name(name)
                .email(email)
                .phone("+254712345678")
                .build();
        customer.setCreatedAt(Instant.now());
        customer.setUpdatedAt(Instant.now());
        customer.setDeleted(deleted);
        customer.setDeletedAt(deleted ? Instant.now() : null);
        return customer;
    }
}

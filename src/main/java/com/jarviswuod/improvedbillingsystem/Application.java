package com.jarviswuod.improvedbillingsystem;

import com.github.javafaker.Faker;
import com.jarviswuod.improvedbillingsystem.customer.Customer;
import com.jarviswuod.improvedbillingsystem.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            CustomerRepository customerRepository
    ) {
        return args -> {
            for (int i = 0; i < 10; i++) {

                Faker faker = new Faker();
                Customer customer = Customer.builder()
                        .name(faker.name().fullName())
                        .email(faker.internet().emailAddress())
                        .phone("+254" + faker.number().digits(9))
                        .build();

                // customerRepository.save(customer);

            }
        };
    }

}

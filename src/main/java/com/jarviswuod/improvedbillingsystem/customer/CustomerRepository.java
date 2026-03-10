package com.jarviswuod.improvedbillingsystem.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {


    @Query(value = "SELECT COUNT(*) > 0 FROM customers WHERE email = :email", nativeQuery = true)
    boolean existsByEmailIncludingDeleted(@Param("email") String email);


    @Query(value = "SELECT * FROM customers WHERE id = :id AND is_deleted = true", nativeQuery = true)
    Optional<Customer> findByIdInDeleted(@Param("id") Long id);


    @Query(value = "SELECT * FROM customers WHERE is_deleted = true", nativeQuery = true)
    List<Customer> findAllDeleted();


    @Modifying
    @Query(value = "DELETE FROM customers WHERE id = :id AND is_deleted = true", nativeQuery = true)
    int permanentlyDeleteById(@Param("id") Long id);

}
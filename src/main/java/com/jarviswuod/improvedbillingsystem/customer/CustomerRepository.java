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

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM Customer u WHERE u.email = :email")
    boolean existsByEmailIncludingDeleted(@Param("email") String email);

//    @Query("SELECT u FROM Customer u WHERE u.id = :id AND u.deleted = true")
    @Query(value = "SELECT * FROM customers WHERE id = :id AND is_deleted = true", nativeQuery = true)
    Optional<Customer> findByIdInDeleted(@Param("id") Long id);


    //    @Query("SELECT u FROM Customer u WHERE u.deleted = true")
    @Query(value = "SELECT * FROM customers WHERE is_deleted = true", nativeQuery = true)
    List<Customer> findAllDeleted();

    @Modifying
    @Query("DELETE FROM Customer u WHERE u.id = :id AND u.deleted = true")
    int permanentlyDeleteById(@Param("id") Long id);

//    Customer findByIdExcludeDeleted(Long id);

//    Customer findByIdIsDeleted(Long id);

}
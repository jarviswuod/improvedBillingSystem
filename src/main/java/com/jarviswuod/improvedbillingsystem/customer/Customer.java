package com.jarviswuod.improvedbillingsystem.customer;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jarviswuod.improvedbillingsystem.invoice.Invoice;
import com.jarviswuod.improvedbillingsystem.utils.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customers")
@Builder
@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "UPDATE customers SET is_deleted = true, deleted_at = NOW() WHERE id = ?")
public class Customer extends BaseEntity {

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String email;

    @Column(length = 20)
    private String phone;

    @OneToMany(mappedBy = "customer")
    @JsonManagedReference
    private List<Invoice> invoice;
}

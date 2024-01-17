package com.kinan.customerapp.repositories;

import com.kinan.customerapp.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Eren
 **/
@Repository
public interface ICustomerRepository extends JpaRepository<Customer, Long> {
}

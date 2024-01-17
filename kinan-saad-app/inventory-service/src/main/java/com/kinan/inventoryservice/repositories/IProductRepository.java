package com.kinan.inventoryservice.repositories;

import com.kinan.inventoryservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Eren
 **/
public interface IProductRepository extends JpaRepository<Product, String> {
}

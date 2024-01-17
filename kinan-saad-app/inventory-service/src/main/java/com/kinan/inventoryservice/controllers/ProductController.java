package com.kinan.inventoryservice.controllers;

import com.kinan.inventoryservice.models.Product;
import com.kinan.inventoryservice.repositories.IProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Eren
 **/
@RestController
@AllArgsConstructor
public class ProductController {
    private IProductRepository productRepository;
    @GetMapping("/products")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public List<Product> getProducts(){
        return productRepository.findAll();
    }

}

package com.kinan.inventoryservice;

import com.kinan.inventoryservice.models.Product;
import com.kinan.inventoryservice.repositories.IProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}
	@Bean
	public CommandLineRunner commandLineRunner(IProductRepository productRepository){
		return args -> {
			productRepository.saveAll(List.of(
					Product.builder().name("Computer").price(22000D).quantity(25).build(),
					Product.builder().name("Iphone 12S").price(12500D).quantity(154).build(),
					Product.builder().name("Printer").price(4500D).quantity(12).build()
			));
		};
	}

}

package com.kinan.customerapp;

import com.kinan.customerapp.models.Customer;
import com.kinan.customerapp.repositories.ICustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CustomerAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerAppApplication.class, args);
    }
    @Bean
    public CommandLineRunner commandLineRunner(ICustomerRepository customerRepository){
        return args -> {
            customerRepository.saveAll(
                    List.of(
                            Customer.builder().name("Saad").email("e.saad.kinan@gmail.com").build(),
                            Customer.builder().name("Yousr").email("yousr@gmail.com").build(),
                            Customer.builder().name("Kawtar").email("kawtar@gmail.com").build()
                    )
            );
        };
    }

}

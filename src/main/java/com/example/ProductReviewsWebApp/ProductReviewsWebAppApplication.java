package com.example.ProductReviewsWebApp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.ProductReviewsWebApp.products.Product;
import com.example.ProductReviewsWebApp.products.ProductRepository;

@SpringBootApplication
public class ProductReviewsWebAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductReviewsWebAppApplication.class, args);
	}

	@Bean
	public CommandLineRunner baseProducts(ProductRepository repository) {
		return (args) -> {
			repository.save(new Product("www.helloworld.com", "Hello World", "Test"));
			repository.save(new Product("www.helloworld.com", "Trong", "Student"));
			repository.save(new Product("www.helloworld.com", "Evan", "Student"));
			repository.save(new Product("www.helloworld.com", "Jeremy", "Student"));
			repository.save(new Product("www.helloworld.com", "Hussein", "Student"));
			repository.save(new Product("www.helloworld.com", "Nathan", "Student"));
		};
	}

}

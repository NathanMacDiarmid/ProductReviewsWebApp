package com.example.ProductReviewsWebApp;

import com.github.javafaker.Faker;
import java.util.concurrent.ThreadLocalRandom;

import com.example.ProductReviewsWebApp.models.Client;
import com.example.ProductReviewsWebApp.repositories.ClientRepository;
import com.example.ProductReviewsWebApp.models.Review;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.ProductReviewsWebApp.models.Product;
import com.example.ProductReviewsWebApp.repositories.ProductRepository;

@SpringBootApplication
public class ProductReviewsWebAppApplication {

	public static final int TEST_PRODUCTS = 10;
	public static final int TEST_CLIENTS = 5;

	public static void main(String[] args) {
		SpringApplication.run(ProductReviewsWebAppApplication.class, args);
	}

	@Bean
	public CommandLineRunner baseInformation(ProductRepository repository, ClientRepository clientRepository) {
		return (args) -> {
			Faker faker = new Faker();

			for (int i = 0; i < TEST_PRODUCTS; i++) {
				String item = faker.space().moon();
				String category = faker.space().constellation();
				Product product = new Product("www.google.com", item, category);


				for (int j = 0; j < TEST_CLIENTS; j++) {
					String name = faker.funnyName().name();
					String description = faker.hobbit().quote();
					int rating = ThreadLocalRandom.current().nextInt(0, 5 + 1);

					Client client = new Client(name);
					Review review = new Review(rating, description);
					client.addReviewForProduct(product.getId(), review);
					product.addReview(review);
					//clientRepository.save(client); // NOT WORKING
				}
				repository.save(product);
			}
		};
	}

}

package com.example.ProductReviewsWebApp;

import com.example.ProductReviewsWebApp.repositories.ReviewRepository;
import com.github.javafaker.Faker;

import java.util.ArrayList;
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
	public CommandLineRunner baseInformation(ProductRepository productRepository, ClientRepository clientRepository, ReviewRepository reviewRepository) {
		return (args) -> {
			Faker faker = new Faker();

			ArrayList<Client> clients = new ArrayList<>();
			ArrayList<Product> products = new ArrayList<>();
			ArrayList<Review> reviews = new ArrayList<>();

			// Create some clients
			for (int i = 0; i < TEST_CLIENTS; i++) {
				String name = faker.funnyName().name();

				Client client = new Client(name);
				clientRepository.save(client);
				clients.add(client);
			}

			// Create some products
			for (int i = 0; i < TEST_PRODUCTS; i++) {
				String item = faker.space().moon();
				String category = faker.space().constellation();

				Product product = new Product("www.google.com", item, category);
				productRepository.save(product);
				products.add(product);
			}

			// Create reviews for each client
			for (Client client : clients) {
				int randomReview = ThreadLocalRandom.current().nextInt(0, 10 + 1);
				int randomProduct = ThreadLocalRandom.current().nextInt(0, products.size());

				for (int i = 0; i < randomReview; i++) {
					Product product = products.get(randomProduct);
					int rating = ThreadLocalRandom.current().nextInt(0, 5 + 1);
					String comment = faker.hobbit().quote();

					Review review = new Review(client, product, rating, comment);
					reviewRepository.save(review);
					reviews.add(review);
				}
			}
		};
	}

}

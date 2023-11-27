package com.example.ProductReviewsWebApp;

import com.example.ProductReviewsWebApp.models.Client;
import com.example.ProductReviewsWebApp.models.Product;
import com.example.ProductReviewsWebApp.models.Review;
import com.example.ProductReviewsWebApp.repositories.ClientRepository;
import com.example.ProductReviewsWebApp.repositories.ProductRepository;
import com.example.ProductReviewsWebApp.repositories.ReviewRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
public class ProductReviewsWebAppApplication {

	public static final int TEST_REVIEWS = 10;
	public static final int TEST_CLIENTS = 5;

	public static void main(String[] args) {
		SpringApplication.run(ProductReviewsWebAppApplication.class, args);
	}

	@Bean
	public CommandLineRunner baseInformation(ProductRepository productRepository,
											 ClientRepository clientRepository,
											 ReviewRepository reviewRepository) {
		return (args) -> {
			Faker faker = new Faker();

			ArrayList<Client> clients = new ArrayList<>();
			ArrayList<Product> products;
			ArrayList<Review> reviews = new ArrayList<>();

			Client jeremy = new Client("Jeremy"); // Temporary until we can get the id of the logged in user
			Client evan = new Client("Evan"); // Temporary until we can get the id of the logged in user
			Client nathan = new Client("Nathan"); // Temporary until we can get the id of the logged in user
			Client trong = new Client("Trong"); // Temporary until we can get the id of the logged in user
			Client hussein = new Client("Hussein"); // Temporary until we can get the id of the logged in user

			clientRepository.save(jeremy);
			clients.add(jeremy);
			clientRepository.save(evan);
			clients.add(evan);
			clientRepository.save(nathan);
			clients.add(nathan);
			clientRepository.save(trong);
			clients.add(trong);
			clientRepository.save(hussein);
			clients.add(hussein);


			// Create some additional clients
			for (int i = 0; i < TEST_CLIENTS; i++) {
				String name = faker.funnyName().name();

				Client client = new Client(name);
				try {
					clientRepository.save(client);
					clients.add(client);
				} catch (Exception ignored) {

				}
			}

			// Create some products
			products = Product.createProductsFromJSON("products.json");
			productRepository.saveAll(products);

			int randomClient;
			int randomReview;
			int randomProduct;
			int randomFollow;
			int rating;
			Review review;
			Product product;
			ArrayList<Integer> alreadyReviewed, alreadyFollowed;
			int index = 0;

			for (Client client : clients) {
				randomReview = ThreadLocalRandom.current().nextInt(TEST_REVIEWS + 1);

				alreadyReviewed = new ArrayList<>();
				alreadyFollowed = new ArrayList<>();
				alreadyFollowed.add(index);
				index++;

				// Create reviews for each client
				for (int i = 0; i < randomReview; i++) {
					randomProduct = ThreadLocalRandom.current().nextInt(products.size());
					String comment = faker.hobbit().quote();
					rating = ThreadLocalRandom.current().nextInt(1, 5 + 1);

					// change up the random product
					while (alreadyReviewed.contains(randomProduct)) {
						randomProduct = ThreadLocalRandom.current().nextInt(products.size());
					}

					product = products.get(randomProduct);
					alreadyReviewed.add(randomProduct);

					review = new Review(rating, comment, product, client.getId());
					client.addReviewForProduct(product.getId(), review);
					reviewRepository.save(review);
					reviews.add(review);
				}

				// Create following for each client
				randomFollow = ThreadLocalRandom.current().nextInt(clients.size() - 1);
				for (int i = 0; i < randomFollow; i++) {
					randomClient = ThreadLocalRandom.current().nextInt(clients.size());

					while (alreadyFollowed.contains(randomClient)) {
						randomClient = ThreadLocalRandom.current().nextInt(clients.size());
					}
					alreadyFollowed.add(randomClient);
					clients.get(randomClient).followUser(client);
				}
			}
			clientRepository.saveAll(clients);
			productRepository.saveAll(products);
			reviewRepository.saveAll(reviews);
		};
	}

}

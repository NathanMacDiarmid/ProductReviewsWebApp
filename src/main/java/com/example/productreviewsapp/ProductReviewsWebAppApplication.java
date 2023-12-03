package com.example.productreviewsapp;

import com.example.productreviewsapp.models.Client;
import com.example.productreviewsapp.models.Product;
import com.example.productreviewsapp.models.Review;
import com.example.productreviewsapp.repositories.ClientRepository;
import com.example.productreviewsapp.repositories.ProductRepository;
import com.example.productreviewsapp.repositories.ReviewRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * SpringBootApplication entry.
 */
@SpringBootApplication
public class ProductReviewsWebAppApplication {

    public static final int TEST_REVIEWS = 10;
    public static final int TEST_CLIENTS = 5;

    /**
     * Main entry.
     * @param args String[]
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductReviewsWebAppApplication.class, args);
    }

    /**
     * Initialization bean to populate the application.
     * @param productRepository ProductRepository
     * @param clientRepository ClientRepository
     * @param reviewRepository ReviewRepository
     * @return CommandLineRunner
     */
    @Bean
    @Profile("!test")
    public CommandLineRunner baseInformation(ProductRepository productRepository,
                                             ClientRepository clientRepository,
                                             ReviewRepository reviewRepository) {
        return (args) -> {
            Faker faker = new Faker();

            ArrayList<Client> clients = new ArrayList<>();
            ArrayList<Client> realClients = new ArrayList<>();
            ArrayList<Product> products;
            ArrayList<Review> reviews = new ArrayList<>();

            Client jeremy = new Client("Jeremy");
            Client evan = new Client("Evan");
            Client nathan = new Client("Nathan");
            Client trong = new Client("Trong");
            Client hussein = new Client("Hussein");

            clientRepository.save(jeremy);
            clients.add(jeremy);
            realClients.add(jeremy);
            clientRepository.save(evan);
            clients.add(evan);
            realClients.add(evan);
            clientRepository.save(nathan);
            clients.add(nathan);
            realClients.add(nathan);
            clientRepository.save(trong);
            clients.add(trong);
            realClients.add(trong);
            clientRepository.save(hussein);
            clients.add(hussein);
            realClients.add(hussein);

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

                // Create reviews for each fake client
                if (!realClients.contains(client)) {
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

                        review = new Review(rating, comment, product, client);
                        client.addReviewForProduct(product.getId(), review);
                        reviewRepository.save(review);
                        reviews.add(review);
                    }
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

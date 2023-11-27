package com.example.ProductReviewsWebApp;

import com.example.ProductReviewsWebApp.models.Category;
import com.example.ProductReviewsWebApp.models.Client;
import com.example.ProductReviewsWebApp.models.Product;
import com.example.ProductReviewsWebApp.models.Review;
import com.example.ProductReviewsWebApp.repositories.ProductRepository;
import com.example.ProductReviewsWebApp.repositories.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReviewTest {
    @Value(value="${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Tests the number of total reviews
     */
    @Test
    public void getNumOfReviewsTest() {
        Client author = new Client("author");

        Product plant1 = new Product("www.coolplants.com", "Basil Plant", Category.PLANT);
        productRepository.save(plant1);
        Review review1 = new Review(5, "Great plant. Doesn't require a lot of maintenance and it's nice to look at.", plant1, author.getId());
        review1.setForTesting();
        Review review2 = new Review(1, "Plant died after a few days.", plant1, author.getId());
        review2.setForTesting();
        Review review3 = new Review(3, "I received a lemon basil plant instead of the Italian one that I ordered", plant1, author.getId());
        review3.setForTesting();

        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);

        int count = 0;

        // GIVEN
        String resourceUrl = "http://localhost:" + port + "/api/review";

        // WHEN
        ResponseEntity<Iterable<Review>> response =
                restTemplate.exchange(resourceUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        for (Review review : Objects.requireNonNull(response.getBody())) {
            if (review.isForTesting())
                count++;
        }
        assertEquals(3, count);

        reviewRepository.delete(review1);
        reviewRepository.delete(review2);
        reviewRepository.delete(review3);
    }

    /**
     * Gets the content of a review
     */
    @Test
    public void getReviewContentTest() {
        Client author = new Client("author");
        Product plant1 = new Product("www.coolplants.com", "Basil Plant", Category.PLANT);

        productRepository.save(plant1);
        Review review = new Review(5, "Great plant. Doesn't require a lot of maintenance and it's nice to look at.", plant1, author.getId());
        reviewRepository.save(review);
        long reviewId = review.getId();

        // GIVEN
        String resourceUrl = "http://localhost:" + port + "/api/review/" + reviewId;

        // WHEN
        ResponseEntity<Review> response = restTemplate.getForEntity(resourceUrl, Review.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(5, Objects.requireNonNull(response.getBody()).getRating());
        assertEquals("Great plant. Doesn't require a lot of maintenance and it's nice to look at.", response.getBody().getComment());

        reviewRepository.delete(review);
    }


    /**
     * Tests deleting a review
     */
    @Test
    public void deleteReviewTest() {
        Client author = new Client("author");
        Product plant1 = new Product("www.coolplants.com", "Basil Plant", Category.PLANT);
        productRepository.save(plant1);
        Review review = new Review(5, "This is a fake review", plant1, author.getId());
        reviewRepository.save(review);
        long reviewId = review.getId();

        // GIVEN
        String resourceUrl = "http://localhost:" + port + "/api/review/" + reviewId;

        // WHEN
        ResponseEntity<Review> response = restTemplate.getForEntity(resourceUrl, Review.class);
        assertEquals("This is a fake review", Objects.requireNonNull(response.getBody()).getComment());
        restTemplate.delete(resourceUrl);

        // THEN
        assertTrue(reviewRepository.findById(reviewId).isEmpty());

        reviewRepository.delete(review);
    }

    /**
     * Tests updating a review
     */
    @Test
    public void updateReviewTest() {
        Client author = new Client("author");
        Product plant1 = new Product("www.coolplants.com", "Basil Plant", Category.PLANT);
        productRepository.save(plant1);
        Review review = new Review(4, "Could be better", plant1, author.getId());
        reviewRepository.save(review);

        long reviewId = review.getId();

        // GIVEN
        HttpEntity<Review> updatedEntity = new HttpEntity<>(new Review(3, "Could be better", plant1, author.getId()));
        String resourceUrl = "http://localhost:" + port + "/api/review/" + reviewId;

        // WHEN
        ResponseEntity<Review> response = restTemplate.exchange(resourceUrl, HttpMethod.PUT, updatedEntity, Review.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(3, Objects.requireNonNull(response.getBody()).getRating());
        assertEquals("Could be better", Objects.requireNonNull(response.getBody()).getComment());

        reviewRepository.delete(review);
    }
}
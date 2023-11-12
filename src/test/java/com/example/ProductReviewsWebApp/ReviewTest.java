package com.example.ProductReviewsWebApp;

import com.example.ProductReviewsWebApp.models.Review;
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

    /**
     * Tests the number of total reviews
     */
    @Test
    public void getNumOfReviewsTest() {
        Review review1 = new Review(5, "Great plant. Doesn't require a lot of maintenance and it's nice to look at.");
        review1.setForTesting();
        Review review2 = new Review(1, "Plant died after a few days.");
        review2.setForTesting();
        Review review3 = new Review(3, "I received a lemon basil plant instead of the Italian one that I ordered");
        review3.setForTesting();

        reviewRepository.save(review1);
        reviewRepository.save(review2);
        reviewRepository.save(review3);

        int count = 0;

        // GIVEN
        String resourceUrl = "http://localhost:" + port + "/review";

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
        Review review = new Review(5, "Great plant. Doesn't require a lot of maintenance and it's nice to look at.");
        reviewRepository.save(review);
        long reviewId = review.getId();

        // GIVEN
        String resourceUrl = "http://localhost:" + port + "/review/" + reviewId;

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

        Review review = new Review(5, "This is a fake review");
        reviewRepository.save(review);
        long reviewId = review.getId();

        // GIVEN
        String resourceUrl = "http://localhost:" + port + "/review/" + reviewId;

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
    public void updateProductTest() {

        Review review = new Review(4, "Could be better");
        reviewRepository.save(review);

        long reviewId = review.getId();

        // GIVEN
        HttpEntity<Review> updatedEntity = new HttpEntity<>(new Review(3, "Could be better"));
        String resourceUrl = "http://localhost:" + port + "/review/" + reviewId;

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
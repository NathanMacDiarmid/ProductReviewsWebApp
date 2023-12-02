package com.example.ProductReviewsWebApp;

import com.example.ProductReviewsWebApp.models.Category;
import com.example.ProductReviewsWebApp.models.Client;
import com.example.ProductReviewsWebApp.models.Product;
import com.example.ProductReviewsWebApp.models.Review;
import com.example.ProductReviewsWebApp.repositories.ClientRepository;
import com.example.ProductReviewsWebApp.repositories.ReviewRepository;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ReviewRestControllerTest {

    @Value(value="${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private ClientRepository clientRepository;

    private List<Review> reviews;

    @PostConstruct
    public void setup() {
        Client author = new Client("author");
        author.setId(1L);
        Product plant1 = new Product("www.cool-plants.com", "Basil Plant", Category.PLANT);
        plant1.setId(1L);
        this.reviews = List.of(
                new Review(5, "Great plant. Doesn't require a lot of maintenance and it's nice to look at.", plant1, author),
                new Review(1, "Plant died after a few days.", plant1, author),
                new Review(3, "I received a lemon basil plant instead of the Italian one that I ordered", plant1, author)
        );

        long id = 0L;
        for (Review review : reviews) {
            review.setId(id++);
            when(reviewRepository.findById(id - 1)).thenReturn(Optional.of(review));
            when(reviewRepository.save(review)).thenReturn(review);
        }

        when(reviewRepository.findAll()).thenReturn(this.reviews);
    }

    @Test
    public void getReviewsTest() {

        // GIVEN
        String resourceUrl = "http://localhost:" + port + "/api/review";

        // WHEN
        ResponseEntity<Iterable<Review>> response =
                restTemplate.exchange(resourceUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        Iterator<Review> responseReviews = Objects.requireNonNull(response.getBody()).iterator();

        for (Review expectedReview : reviews) {
            assertEquals(expectedReview, responseReviews.next());
        }
    }

    @Test
    public void getReviewByIdTest() {

        // GIVEN
        int index = 1;
        long reviewId = reviews.get(index).getId();
        String resourceUrl = "http://localhost:" + port + "/api/review/" + reviewId;
        System.out.println(resourceUrl);

        // WHEN
        ResponseEntity<Review> response = restTemplate.getForEntity(resourceUrl, Review.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(reviews.get(index), response.getBody());
    }

    @Test
    public void createReviewTest() {

        Client client = new Client();
        client.setId(7L);
        Review review = new Review(5, "comment", new Product(), client);
        review.setId(7L);
        when(reviewRepository.save(review)).thenReturn(review);

        // GIVEN
        String resourceUrl = "http://localhost:" + port + "/api/review";
        HttpEntity<Review> updatedEntity = new HttpEntity<>(review);

        // WHEN
        ResponseEntity<Review> response = restTemplate.exchange(resourceUrl, HttpMethod.POST, updatedEntity, Review.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(review, response.getBody());
    }

    @Test
    public void addReviewTest() {
        // TODO
    }

    @Test
    public void deleteReviewTest() {

        // GIVEN
        int index = 1;
        long reviewId = reviews.get(index).getId();
        String resourceUrl = "http://localhost:" + port + "/api/review/" + reviewId;

        // WHEN
        ResponseEntity<Review> response = restTemplate.getForEntity(resourceUrl, Review.class);
        restTemplate.delete(resourceUrl);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(reviews.get(index), response.getBody());
    }

    @Test
    public void updateReviewTest() {

        // GIVEN
        int index = 2;
        HttpEntity<Review> updatedEntity = new HttpEntity<>(reviews.get(index));
        String resourceUrl = "http://localhost:" + port + "/api/review/" + reviews.get(index).getId();

        // WHEN
        ResponseEntity<Review> response = restTemplate.exchange(resourceUrl, HttpMethod.PUT, updatedEntity, Review.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        System.out.println(response);
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(reviews.get(index), response.getBody());
    }
}
package com.example.ProductReviewsWebApp;

import com.example.ProductReviewsWebApp.models.Category;
import com.example.ProductReviewsWebApp.models.Product;
import com.example.ProductReviewsWebApp.repositories.ProductRepository;
import com.example.ProductReviewsWebApp.models.Review;
import com.example.ProductReviewsWebApp.models.Client;
import com.example.ProductReviewsWebApp.repositories.ClientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClientTest {

    @Value(value="${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductRepository productRepository;

    private Client tom;

    private Client jerry;

    private Product burger;

    private Product hotdog;

    @BeforeEach
    public void setup() {
        //Add two products to the list;
        burger = new Product("www.burger.com", "burger", Category.FOOD);
        hotdog = new Product("www.hotdog.com", "hotdog", Category.FOOD);
        productRepository.save(burger);
        productRepository.save(hotdog);

        // Add two identical users to the User List
        tom = new Client("Tom");
        Review tomsReview1 = new Review(3, "Burger was ok!", burger);
        Review tomsReview2 = new Review(1, "Worst Hot Dog Ever!", hotdog);
        tom.addReviewForProduct(burger.getId(), tomsReview1);
        tom.addReviewForProduct(hotdog.getId(), tomsReview2);

        jerry = new Client("Jerry");
        Review jerrysReview1 = new Review(3, "Burger was mehhh", burger);
        Review jerrysReview2 = new Review(1, "Worst Hot Dog OF ALL TIME!", hotdog);
        jerry.addReviewForProduct(burger.getId(), jerrysReview1);
        jerry.addReviewForProduct(hotdog.getId(), jerrysReview2);

        productRepository.save(burger);
        productRepository.save(hotdog);
        clientRepository.save(tom);
        clientRepository.save(jerry);
    }

    @AfterEach
    public void tearDown() {
        clientRepository.delete(tom);
        clientRepository.delete(jerry);
    }

    @Test
    public void removeReviewForProduct() {
        // Ensure the setup added reviews are there.
        assertTrue(tom.hasReviewForProduct(burger.getId()));
        assertTrue(tom.hasReviewForProduct(hotdog.getId()));
        assertTrue(jerry.hasReviewForProduct(burger.getId()));
        assertTrue(jerry.hasReviewForProduct(hotdog.getId()));

        tom.removeReviewForProduct(burger.getId());

        assertFalse(tom.hasReviewForProduct(burger.getId()));
    }

    @Test
    public void testFollowingUnfollowingOperation() {
        // Ensure base case.
        assertEquals(0, tom.getFollowerCount());
        assertFalse(jerry.getFollowing().contains(tom));

        // Jerry Follows Tom
        assertTrue(jerry.followUser(tom));

        // Ensure correct values.
        assertEquals(1, tom.getFollowerCount());
        assertTrue(jerry.getFollowing().contains(tom));

        // Jerry Tries to Follow Tom Again
        assertFalse(jerry.followUser(tom));

        // Ensure correct values.
        assertEquals(1, tom.getFollowerCount());
        assertTrue(jerry.getFollowing().contains(tom));

        // Jerry Unfollows Tom
        assertTrue(jerry.unfollowUser(tom));

        // Ensure return base case.
        assertEquals(0, tom.getFollowerCount());
        assertFalse(jerry.getFollowing().contains(tom));

        // Jerry Tries to Unfollow Tom Again
        assertFalse(jerry.unfollowUser(tom));

        // Ensure return base case.
        assertEquals(0, tom.getFollowerCount());
        assertFalse(jerry.getFollowing().contains(tom));
    }

    @Test
    public void testJaccardDistanceCalculation() {
        // Check that base case of identical review scores holds true.
        assertEquals(1, tom.getJaccardDistanceWithUser(jerry));

        // Change one of tom's review scores
        tom.getReviewForProduct(burger.getId()).setRating(1);

        // Check new result
        assertEquals(0.34, tom.getJaccardDistanceWithUser(jerry));

        // Change the other score
        tom.getReviewForProduct(hotdog.getId()).setRating(4);

        // Check new result
        assertEquals(0, tom.getJaccardDistanceWithUser(jerry));
    }

    @Test
    public void getClientsTest() {
        // GIVEN
        String resourceUrl = "http://localhost:" + port + "/api/client";


        // WHEN
        ResponseEntity<Iterable<Client>> response =
                restTemplate.exchange(resourceUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
    }

    @Test
    public void getClientByIdTest() {
        // GIVEN
        String resourceUrl = "http://localhost:" + port + "/api/client/" + tom.getId();

        // WHEN
        ResponseEntity<Client> response = restTemplate.getForEntity(resourceUrl, Client.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("Tom", Objects.requireNonNull(response.getBody()).getUsername());
    }
}
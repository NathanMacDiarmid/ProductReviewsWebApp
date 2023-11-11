package com.example.ProductReviewsWebApp;

import com.example.ProductReviewsWebApp.products.Product;
import com.example.ProductReviewsWebApp.reviews.Review;
import com.example.ProductReviewsWebApp.clients.Client;
import com.example.ProductReviewsWebApp.clients.ClientRepository;
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

import java.math.BigDecimal;
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

    private Client tom;

    private Client jerry;

    @BeforeEach
    public void setup() {
        // Add two identical users to the User List
        tom = new Client("Tom");
        tom.addReviewForProduct(1L, new Review(3, "Pizza was ok!"));
        tom.addReviewForProduct(2L, new Review(1, "Worst Hot Dog Ever!"));

        jerry = new Client("Jerry");
        jerry.addReviewForProduct(1L, new Review(3, "Pizza was mehhh"));
        jerry.addReviewForProduct(2L, new Review(1, "Worst Hot Dog OF ALL TIME!"));

        clientRepository.save(tom);
        clientRepository.save(jerry);
    }

    @AfterEach
    public void tearDown() {
        // Clear the user repository.
        clientRepository.deleteAll();
    }

    @Test
    public void removeReviewForProduct() {
        // Ensure the setup added reviews are there.
        assertTrue(tom.hasReviewForProduct(1L));
        assertTrue(tom.hasReviewForProduct(2L));
        assertTrue(jerry.hasReviewForProduct(1L));
        assertTrue(jerry.hasReviewForProduct(2L));

        tom.removeReviewForProduct(1L);

        assertFalse(tom.hasReviewForProduct(1L));
    }

    @Test
    public void testFollowingUnfollowingOperation() {
        // Ensure base case.
        assertEquals(0, tom.getFollowerCount());
        assertFalse(jerry.getFollowingList().contains(tom));

        // Jerry Follows Tom
        assertTrue(jerry.followUser(tom));

        // Ensure correct values.
        assertEquals(1, tom.getFollowerCount());
        assertTrue(jerry.getFollowingList().contains(tom));

        // Jerry Tries to Follow Tom Again
        assertFalse(jerry.followUser(tom));

        // Ensure correct values.
        assertEquals(1, tom.getFollowerCount());
        assertTrue(jerry.getFollowingList().contains(tom));

        // Jerry Unfollows Tom
        assertTrue(jerry.unfollowUser(tom));

        // Ensure return base case.
        assertEquals(0, tom.getFollowerCount());
        assertFalse(jerry.getFollowingList().contains(tom));

        // Jerry Tries to Unfollow Tom Again
        assertFalse(jerry.unfollowUser(tom));

        // Ensure return base case.
        assertEquals(0, tom.getFollowerCount());
        assertFalse(jerry.getFollowingList().contains(tom));
    }

    @Test
    public void testJaccardDistanceCalculation() {
        // Check that base case of identical review scores holds true.
        assertEquals(1, tom.getJaccardDistanceWithUser(jerry));

        // Change one of tom's review scores
        tom.getReviewForProduct(1L).setRating(1);

        // Check new result
        assertEquals(0.34, tom.getJaccardDistanceWithUser(jerry));

        // Change the other score
        tom.getReviewForProduct(2L).setRating(4);

        // Check new result
        assertEquals(0, tom.getJaccardDistanceWithUser(jerry));
    }

    @Test
    public void getClientsTest() {
        // GIVEN
        String resourceUrl = "http://localhost:" + port + "/client";

        // WHEN
        ResponseEntity<Iterable<Client>> response =
                restTemplate.exchange(resourceUrl, HttpMethod.GET, null, new ParameterizedTypeReference<Iterable<Client>>() {});

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        Objects.requireNonNull(
                response.getBody()).forEach(
                client -> assertTrue((Objects.equals(client.getUsername(), "Tom")) || (Objects.equals(client.getUsername(), "Jerry"))));
    }

    @Test
    public void getClientByIdTest() {
        // GIVEN
        String resourceUrl = "http://localhost:" + port + "/client/1";

        // WHEN
        ResponseEntity<Client> response = restTemplate.getForEntity(resourceUrl, Client.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("Tom", Objects.requireNonNull(response.getBody()).getUsername());
    }
}
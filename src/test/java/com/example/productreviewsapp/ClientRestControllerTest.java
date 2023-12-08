package com.example.productreviewsapp;

import com.example.productreviewsapp.models.*;
import com.example.productreviewsapp.repositories.ClientRepository;
import com.example.productreviewsapp.repositories.ProductRepository;
import com.example.productreviewsapp.repositories.ReviewRepository;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Assertions;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ClientRestControllerTest {

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ReviewRepository reviewRepository;

    private static final int numProducts = 2;

    private List<Product> products;

    private List<Client> clients;

    @PostConstruct
    private void setup() {
        this.products = generateProducts();
        this.clients = generateClients();
        List<Review> reviews = generateReviews();

        long clientId = 0L;
        for (Client client : clients) {
            client.setId(clientId++);
            when(clientRepository.findById(clientId - 1)).thenReturn(Optional.of(client));
            when(clientRepository.save(client)).thenReturn(client);
        }
        when(clientRepository.findAll()).thenReturn(this.clients);

        long productId = 0L;
        for (Product product : products) {
            product.setId(productId++);
            when(productRepository.findById(productId - 1)).thenReturn(Optional.of(product));
            when(productRepository.save(product)).thenReturn(product);
        }
        when(productRepository.findAll()).thenReturn(this.products);

        long reviewId = 0L;
        for (Review review : reviews) {
            review.setId(reviewId++);
            when(reviewRepository.findById(reviewId - 1)).thenReturn(Optional.of(review));
            when(reviewRepository.save(review)).thenReturn(review);
        }
        when(reviewRepository.findAll()).thenReturn(reviews);

        clients.get(2).addReviewForProduct(products.get(0).getId(), reviews.get(0));
        clients.get(2).addReviewForProduct(products.get(1).getId(), reviews.get(1));
        clients.get(3).addReviewForProduct(products.get(0).getId(), reviews.get(2));
        clients.get(3).addReviewForProduct(products.get(1).getId(), reviews.get(3));
    }

    private List<Product> generateProducts() {
        ArrayList<Product> productList = new ArrayList<>();
        for (int i = 0; i < numProducts; i++) {
            productList.add(new Product("http://url" + i + ".com", "product" + i, Category.BOOK));
        }
        return productList;
    }

    private List<Client> generateClients() {
        ArrayList<Client> clientList = new ArrayList<>();
        for (String clientName : List.of("Bob", "Alice", "Charlie", "Denise")) {
            clientList.add(new Client(clientName));
        }
        return clientList;
    }

    private List<Review> generateReviews() {
        Review review1 = new Review(3, "Burger was mehhh", products.get(0), clients.get(0));
        Review review2 = new Review(1, "Worst Hot Dog OF ALL TIME!", products.get(1), clients.get(0));

        Review review3 = new Review(3, "Burger was mehhh", products.get(0), clients.get(1));
        Review review4 = new Review(1, "Worst Hot Dog OF ALL TIME!", products.get(1), clients.get(1));
        return List.of(review1, review2, review3, review4);
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
        Iterator<Client> responseClients = Objects.requireNonNull(response.getBody()).iterator();

        for (Client expectedClient : this.clients) {
            assertEquals(expectedClient, responseClients.next());
        }
    }

    @Test
    public void getClientByIdTest() {
        // GIVEN
        int index = 1;
        long clientId = clients.get(index).getId();
        String resourceUrl = "http://localhost:" + port + "/api/client/" + clientId;

        // WHEN
        ResponseEntity<Client> response = restTemplate.getForEntity(resourceUrl, Client.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(clients.get(index), response.getBody());
    }

    @Test
    public void getActiveClientIdTest() {
        // GIVEN
        long activeId = 1L;
        String resourceUrl = "http://localhost:" + port + "/api/activeClientId";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, SystemConstants.ACTIVE_CLIENT_ID_COOKIE + "=" + activeId);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // WHEN
        ResponseEntity<Long> response = restTemplate.exchange(resourceUrl, HttpMethod.GET, request, Long.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(1L, response.getBody());
    }

    @Test
    public void getJaccardDistanceFromUserToActiveUserByIdTest() {
        // GIVEN
        long id = 1L;
        String resourceUrl = "http://localhost:" + port + "/api/client/" + id + "/jaccardDistance";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, SystemConstants.ACTIVE_CLIENT_ID_COOKIE + "=" + id);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // WHEN
        ResponseEntity<Double> response = restTemplate.exchange(resourceUrl, HttpMethod.GET, request, Double.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(0, response.getBody());
    }

    @Test
    public void followTest() {

        // GIVEN
        long idOfClientToFollow = 1L;
        long activeClientId = 0L;
        String unfollowResourceUrl = "http://localhost:" + port + "/api/client/" + idOfClientToFollow + "/unfollow";
        String resourceUrl = "http://localhost:" + port + "/api/client/" + idOfClientToFollow + "/follow";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, SystemConstants.ACTIVE_CLIENT_ID_COOKIE + "=" + activeClientId);
        HttpEntity<String> request = new HttpEntity<>(headers);
        assertEquals(HttpStatus.OK, restTemplate.exchange(unfollowResourceUrl, HttpMethod.POST, request, Void.class).getStatusCode());

        // WHEN
        ResponseEntity<Void> response = restTemplate.exchange(resourceUrl, HttpMethod.POST, request, Void.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Optional<Client> activeClient = clientRepository.findById(activeClientId);
        Optional<Client> followedClient = clientRepository.findById(idOfClientToFollow);
        Assertions.assertTrue(activeClient.isPresent());
        Assertions.assertTrue(followedClient.isPresent());
        Assertions.assertTrue(activeClient.get().isFollowing(followedClient.get()));
        Assertions.assertEquals(1, followedClient.get().getFollowerCount());
    }

    @Test
    public void unFollowTest() {
        // GIVEN
        long idOfClientToUnFollow = 1L;
        long activeClientId = 0L;
        String followResourceUrl = "http://localhost:" + port + "/api/client/" + idOfClientToUnFollow + "/follow";
        String resourceUrl = "http://localhost:" + port + "/api/client/" + idOfClientToUnFollow + "/unfollow";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, SystemConstants.ACTIVE_CLIENT_ID_COOKIE + "=" + activeClientId);
        HttpEntity<String> request = new HttpEntity<>(headers);
        assertEquals(HttpStatus.OK, restTemplate.exchange(followResourceUrl, HttpMethod.POST, request, Void.class).getStatusCode());


        // WHEN
        ResponseEntity<Void> response = restTemplate.exchange(resourceUrl, HttpMethod.POST, request, Void.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Optional<Client> activeClient = clientRepository.findById(activeClientId);
        Optional<Client> followedClient = clientRepository.findById(idOfClientToUnFollow);
        Assertions.assertTrue(activeClient.isPresent());
        Assertions.assertTrue(followedClient.isPresent());
        Assertions.assertFalse(activeClient.get().isFollowing(followedClient.get()));
        Assertions.assertEquals(0, followedClient.get().getFollowerCount());
    }

    @Test
    public void testDegreesOfSeparation() {
        // GIVEN
        Client client1 = clients.get(0);
        Client client2 = clients.get(1);
        Client client3 = clients.get(2);
        Client client4 = clients.get(3);

        client1.followUser(client2);
        client2.followUser(client3);

        // degrees of separation between client1 and client2 should be 1
        assertEquals(1, client1.getDegreesOfSeparation(client2));

        // degrees of separation between client1 and client3 should be 2
        assertEquals(2, client1.getDegreesOfSeparation(client3));

        // degrees of separation between client1 and client4 should be 0
        assertEquals(0, client1.getDegreesOfSeparation(client4));

        // test cyclical following
        client3.followUser(client1);
        assertEquals(2, client1.getDegreesOfSeparation(client3));

        // Cleanup
        client1.unfollowUser(client2);
        client2.unfollowUser(client3);
    }

    @Test
    public void testJaccardDistanceCalculation() {

        // GIVEN
        Client client1 = clients.get(2);
        Client client2 = clients.get(3);

        Product product1 = products.get(0);
        Product product2 = products.get(1);

        // Check that base case of identical review scores holds true.
        assertEquals(1, client1.getJaccardDistanceWithUser(client2));

        // Change one of client1's review scores
        client1.getReviewForProduct(product1.getId()).setRating(1);

        // Check new result
        assertEquals(0.34, client1.getJaccardDistanceWithUser(client2));

        // Change the other score
        client1.getReviewForProduct(product2.getId()).setRating(4);

        // Check new result
        assertEquals(0, client1.getJaccardDistanceWithUser(client2));
    }
}
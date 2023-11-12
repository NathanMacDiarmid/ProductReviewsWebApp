package com.example.ProductReviewsWebApp;

import com.example.ProductReviewsWebApp.models.Product;
import com.example.ProductReviewsWebApp.repositories.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductTest {

    @Value(value="${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    private Product pizza;

    private Product shawarma;

    @BeforeEach
    public void setup() {
        // Add two test products to the product list
        pizza = new Product("www.pizza.com", "pizza", "food");
        shawarma = new Product("www.shawarma.com", "shawarma", "food");
        productRepository.save(pizza);
        productRepository.save(shawarma);
    }

    @AfterEach
    public void tearDown() {
        // Clear all persisted products
        productRepository.delete(pizza);
        productRepository.delete(shawarma);
    }

    @Test
    public void getProductsTest() {

        // GIVEN
        String resourceUrl = "http://localhost:" + port + "/product";

        // WHEN
        ResponseEntity<Iterable<Product>> response =
                restTemplate.exchange(resourceUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
    }

    @Test
    public void getProductTest() {
        // GIVEN
        Product testProduct = productRepository.findByName("pizza");
        String resourceUrl = "http://localhost:" + port + "/product/" + testProduct.getId();

        // WHEN
        ResponseEntity<Product> response = restTemplate.getForEntity(resourceUrl, Product.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("pizza", Objects.requireNonNull(response.getBody()).getName());
        assertEquals("www.pizza.com", response.getBody().getUrl());
        assertEquals("food", response.getBody().getCategory());
    }

    @Test
    public void createProductTest() {

        // GIVEN
        HttpEntity<Product> request = new HttpEntity<>(new Product("www.spaghetti.com", "spaghetti", "food"));
        String resourceUrl = "http://localhost:" + port + "/product";

        // WHEN
        ResponseEntity<Product> response = restTemplate.postForEntity(resourceUrl, request, Product.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("spaghetti", Objects.requireNonNull(response.getBody()).getName());
        assertEquals("www.spaghetti.com", response.getBody().getUrl());
        assertEquals("food", response.getBody().getCategory());

        assertNotNull(productRepository.findByName("spaghetti"));

        productRepository.delete(productRepository.findByName("spaghetti"));
    }

    @Test
    public void deleteProductTest() {

        // GIVEN
        String resourceUrl = "http://localhost:" + port + "/product/1";

        // WHEN
        restTemplate.delete(resourceUrl);

        // THEN
        assertTrue(productRepository.findById(1L).isEmpty());
    }

    @Test
    public void updateProductTest() {

        // GIVEN
        HttpEntity<Product> updatedEntity =
                new HttpEntity<>(new Product("www.pizza.com", "piizzzzzaaaa", "food"));
        Long id = productRepository.findByName("pizza").getId();
        String resourceUrl = "http://localhost:" + port + "/product/" + id;

        // WHEN
        ResponseEntity<Product> response = restTemplate.exchange(resourceUrl, HttpMethod.PUT, updatedEntity, Product.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("piizzzzzaaaa", Objects.requireNonNull(response.getBody()).getName());
        assertEquals("www.pizza.com", response.getBody().getUrl());
        assertEquals("food", response.getBody().getCategory());

        productRepository.findById(id).ifPresent(
                product -> assertEquals("piizzzzzaaaa", product.getName())
        );
    }
}

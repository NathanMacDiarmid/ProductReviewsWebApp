package com.example.ProductReviewsWebApp;

import com.example.ProductReviewsWebApp.models.Category;
import com.example.ProductReviewsWebApp.models.Product;
import com.example.ProductReviewsWebApp.repositories.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
public class ProductRestControllerTest {

    @Value(value="${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private static ProductRepository mockProductRepository;

    private List<Product> products;

    @PostConstruct
    private void setup() {
        this.products = generateProducts();

        when(mockProductRepository.findAll()).thenReturn(products);

        for (int i = 0; i < products.size(); i++) {
            when(mockProductRepository.findById((long) i)).thenReturn(Optional.ofNullable(products.get(i)));
            when(mockProductRepository.save(products.get(i))).thenReturn(products.get(i));
        }


    }

    private List<Product> generateProducts() {
        List<Product> productList = List.of(
                new Product("www.pizza.com", "pizza", Category.FOOD),
                new Product("www.spider-plant.com", "spider", Category.PLANT),
                new Product("www.harry-potter.com", "Harry Potter and the Philosopher's Stone", Category.BOOK)
        );

        long id = 0L;
        for (Product product : productList) {
            product.setId(id++);
            System.out.println(product.getId());
        }

        return productList;
    }

    @Test
    public void testGetProducts() {

        // GIVEN
        String resourceUrl = "http://localhost:" + port + "/api/product";

        // WHEN
        ResponseEntity<Iterable<Product>> response =
                restTemplate.exchange(resourceUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void getProductTest(int index) {
        // GIVEN
        Product testProduct = products.get(index);
        String resourceUrl = "http://localhost:" + port + "/api/product/" + testProduct.getId();
        System.out.println(resourceUrl);

        // WHEN
        ResponseEntity<Product> response = restTemplate.getForEntity(resourceUrl, Product.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(testProduct.getName(), Objects.requireNonNull(response.getBody()).getName());
        assertEquals(testProduct.getUrl(), response.getBody().getUrl());
        assertEquals(testProduct.getCategory(), response.getBody().getCategory());
    }

    @Test
    public void createProductTest() {

        // GIVEN
        HttpEntity<Product> request = new HttpEntity<>(new Product("www.spaghetti.com", "spaghetti", Category.FOOD));
        String resourceUrl = "http://localhost:" + port + "/api/product";

        // WHEN
        ResponseEntity<Product> response = restTemplate.postForEntity(resourceUrl, request, Product.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("spaghetti", Objects.requireNonNull(response.getBody()).getName());
        assertEquals("www.spaghetti.com", response.getBody().getUrl());
        assertEquals(Category.FOOD, response.getBody().getCategory());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    public void deleteProductTest(int index) {

        // GIVEN
        HttpEntity<Product> request = new HttpEntity<>(products.get(index));
        String resourceUrl = "http://localhost:" + port + "/api/product";

        ResponseEntity<Product> response = restTemplate.postForEntity(resourceUrl, request, Product.class);

        assertEquals(products.get(index).getName(), Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    public void updateProductTest() {

        // GIVEN
        int index = 0;
        HttpEntity<Product> updatedEntity =
                new HttpEntity<>(products.get(index));
        String resourceUrl = "http://localhost:" + port + "/api/product/" + products.get(index).getId();

        // WHEN
        ResponseEntity<Product> response = restTemplate.exchange(resourceUrl, HttpMethod.PUT, updatedEntity, Product.class);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(products.get(index).getName(), Objects.requireNonNull(response.getBody()).getName());
        assertEquals(products.get(index).getUrl(), response.getBody().getUrl());
        assertEquals(products.get(index).getCategory(), response.getBody().getCategory());
    }
}

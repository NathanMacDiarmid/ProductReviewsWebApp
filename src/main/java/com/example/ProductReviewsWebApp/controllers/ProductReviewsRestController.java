package com.example.ProductReviewsWebApp.controllers;

import com.example.ProductReviewsWebApp.models.Client;
import com.example.ProductReviewsWebApp.models.Product;
import com.example.ProductReviewsWebApp.repositories.ClientRepository;
import com.example.ProductReviewsWebApp.repositories.ProductRepository;
import com.example.ProductReviewsWebApp.models.Review;
import com.example.ProductReviewsWebApp.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping(value="/api")
public class ProductReviewsRestController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ClientRepository clientRepository;

    private Product getProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return product.get();
    }

    private Review getReview(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return review.get();
    }

    private Client getClient(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return client.get();
    }

    private Client findClientById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return client.get();
    }

    @GetMapping("/product")
    public Iterable<Product> getProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/review")
    public Iterable<Review> getReviews() {
        return reviewRepository.findAll();
    }

    @GetMapping("/client")
    public Iterable<Client> getClients() { return clientRepository.findAll(); }

    @GetMapping(value="/product/{id}", produces="application/json")
    public Product getProductById(@PathVariable("id") Long id) {
        return getProduct(id);
    }

    @GetMapping(value="/review/{id}", produces="application/json")
    public Review getReviewById(@PathVariable("id") Long id) {
        return getReview(id);
    }

    @GetMapping(value="/client/{id}", produces = "application/json")
    public Client getClientById(@PathVariable("id") Long id) { return findClientById(id); }

    @GetMapping("/activeClientId")
    public Long getActiveClientId(@CookieValue(value = "activeClientID") String activeClientId) {
        if (!activeClientId.isEmpty())
            return Long.parseLong(activeClientId);

        return 0L;
    }

    @GetMapping(value = "/client/{id}/jaccardDistance", produces = "application/json")
    public double getJaccardDistanceFromUserToActiveUserById(@PathVariable("id") Long id, @CookieValue(value = "activeClientID") String activeClientId) {
        Long activeID;

        if (!activeClientId.isEmpty())
            activeID = Long.parseLong(activeClientId);
        else
            return 0;

        Optional<Client> activeClient = clientRepository.findById(activeID);
        Optional<Client> clientToCompare = clientRepository.findById(id);

        if (activeClient.isPresent() && clientToCompare.isPresent())
            return activeClient.get().getJaccardDistanceWithUser(clientToCompare.get());
        else
            return 0;
    }

    @PostMapping(value="/product", consumes="application/json", produces="application/json")
    public Product createProduct(@RequestBody Product product) {
        productRepository.save(product);
        return product;
    }

    @PostMapping(value="/review", consumes="application/json", produces="application/json")
    public Review createReview(@RequestBody Review review) {
        reviewRepository.save(review);
        return review;
    }

    @PutMapping(value="/product/{id}", consumes="application/json", produces="application/json")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product newProduct) {
        Product product = getProduct(id);
        if (newProduct.getName() != null) {
            product.setName(newProduct.getName());
        }
        if (newProduct.getUrl() != null) {
            product.setUrl(newProduct.getUrl());
        }
        if (newProduct.getCategory() != null) {
            product.setCategory(newProduct.getCategory());
        }
        return productRepository.save(product);
    }

    @PutMapping(value="/product/{id}/addReview", consumes="application/json", produces="application/json")
    public Review addReview(@CookieValue(value = "activeClientID") String activeClientId, @PathVariable Long id, @RequestBody Review review) {
        Client client = getClient(Long.parseLong(activeClientId));
        client.addReviewForProduct(id, review);
        clientRepository.save(client);
        return review;
    }

    @PutMapping(value="/review/{id}", consumes="application/json", produces="application/json")
    public Review updateReview(@PathVariable Long id, @RequestBody Review newReview) {
        Review review = getReview(id);
        review.setRating(newReview.getRating());
        if (newReview.getComment() != null) {
            newReview.setComment(newReview.getComment());
        }
        return reviewRepository.save(review);
    }

    @DeleteMapping(value="/product/{id}")
    public Product deleteProduct(@PathVariable Long id) {
        Product product = getProduct(id);
        productRepository.deleteById(id);
        return product;
    }

    // This method is only for the sake of testing
    // Do not use this method to delete a review from a product
    // or else an error will be thrown
    @DeleteMapping(value="/review/{id}")
    public Review deleteReview(@PathVariable Long id) {
        Review review = getReview(id);
        reviewRepository.deleteById(id);
        return review;
    }
}
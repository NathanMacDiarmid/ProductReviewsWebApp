package com.example.ProductReviewsWebApp.controllers;

import com.example.ProductReviewsWebApp.configuration.SecurityConfig;
import com.example.ProductReviewsWebApp.models.Client;
import com.example.ProductReviewsWebApp.models.Product;
import com.example.ProductReviewsWebApp.repositories.ClientRepository;
import com.example.ProductReviewsWebApp.repositories.ProductRepository;
import com.example.ProductReviewsWebApp.models.Review;
import com.example.ProductReviewsWebApp.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductReviewsController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SecurityConfig securityConfig;

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

    private Client findClientByUsername(String username) {
        Optional<Client> client = Optional.ofNullable(clientRepository.findByUsername(username));
        if (client.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return client.get();
    }

    @GetMapping
    public String index() {
        if (securityConfig.isAuthenticated()) {
            return "redirect:/home";
        }
        return "index";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        if (securityConfig.isAuthenticated()) {
            return "redirect:/home";
        }
        return "login";
    }

    @GetMapping(value="/product")
    public String getProducts(Model model) {
        List<Product> productList = productRepository.findAll();
        model.addAttribute("ProductList", productList);
        return "product";
    }

    @GetMapping(value="/review", produces="application/json")
    public String getReviews(Model model) {
        List<Review> reviewList = reviewRepository.findAll();
        model.addAttribute("ReviewList", reviewList);
        return "review";
    }

    @GetMapping(value = "/client", produces = "application/json")
    public String getClients(Model model) {
        List<Client> clientList = clientRepository.findAll();
        model.addAttribute("ClientList", clientList);
        return "client";
    }

    @GetMapping(value="/product/{id}", produces="application/json")
    public String getProductById(@PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        model.addAttribute("product", product);
        return "product-page";
    }

    @GetMapping(value="/review/{id}", produces="application/json")
    public String getReviewById(@PathVariable("id") Long id, Model model) {
        Review review = getReview(id);
        model.addAttribute("review", review);
        return "review-page";
    }

    @GetMapping(value = "/client/{username}", produces = "application/json")
    public String getClientByUsername(@PathVariable("username") String username, Model model) {
        Client client = this.findClientByUsername(username);
        model.addAttribute("client", client);
        return "client-page";
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
    public Review addReview(@PathVariable Long id, @RequestBody Review review) {
        Product product = getProduct(id);
        product.addReview(review);
        productRepository.save(product);
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

    @DeleteMapping(value="/product/{productId}/{reviewId}")
    public Review deleteReview(@PathVariable Long productId, @PathVariable Long reviewId) {
        Product product = getProduct(productId);
        Review review = getReview(reviewId);
        product.removeReview(review);
        reviewRepository.deleteById(reviewId);
        productRepository.save(product);
        return review;
    }
}

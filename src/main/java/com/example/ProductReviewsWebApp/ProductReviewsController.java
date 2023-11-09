package com.example.ProductReviewsWebApp;

import com.example.ProductReviewsWebApp.products.Product;
import com.example.ProductReviewsWebApp.products.ProductRepository;
import com.example.ProductReviewsWebApp.reviews.Review;
import com.example.ProductReviewsWebApp.reviews.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
public class ProductReviewsController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

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

    @GetMapping("/product")
    public Iterable<Product> getProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/review")
    public Iterable<Review> getReviews() {
        return reviewRepository.findAll();
    }

    @GetMapping(value="/product/{id}", produces="application/json")
    public Product getProductById(@PathVariable("id") Long id) {
        return getProduct(id);
    }

    @GetMapping(value="/review/{id}", produces="application/json")
    public Review getReviewById(@PathVariable("id") Long id) {
        return getReview(id);
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

    @PutMapping(value="/review/{id}", consumes="application/json", produces="application/json")
    public Review updateReview(@PathVariable Long id, @RequestBody Review newReview) {
        Review review = getReview(id);
        review.setRating(newReview.getRating());
        if (newReview.getDescription() != null) {
            newReview.setDescription(newReview.getDescription());
        }
        return reviewRepository.save(review);
    }

    @DeleteMapping(value="/product/{id}")
    public Product deleteProduct(@PathVariable Long id) {
        Product product = getProduct(id);
        productRepository.deleteById(id);
        return product;
    }

    @DeleteMapping(value="/review/{id}")
    public Review deleteReview(@PathVariable Long id) {
        Review review = getReview(id);
        reviewRepository.deleteById(id);
        return review;
    }
}

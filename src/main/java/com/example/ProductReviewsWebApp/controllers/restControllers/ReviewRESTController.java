package com.example.ProductReviewsWebApp.controllers.restControllers;

import com.example.ProductReviewsWebApp.models.Client;
import com.example.ProductReviewsWebApp.models.Review;
import com.example.ProductReviewsWebApp.models.SystemConstants;
import com.example.ProductReviewsWebApp.repositories.ClientRepository;
import com.example.ProductReviewsWebApp.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping(value="/api")
public class ReviewRESTController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ClientRepository clientRepository;

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

    @GetMapping("/review")
    public Iterable<Review> getReviews() {
        return reviewRepository.findAll();
    }

    @GetMapping(value="/review/{id}", produces="application/json")
    public Review getReviewById(@PathVariable("id") Long id) {
        return getReview(id);
    }

    @PostMapping(value="/review", consumes="application/json", produces="application/json")
    public Review createReview(@RequestBody Review review) {
        reviewRepository.save(review);
        return review;
    }

    @PutMapping(value="/product/{id}/addReview", consumes="application/json", produces="application/json")
    public Review addReview(@CookieValue(value = SystemConstants.ACTIVE_CLIENT_ID_COOKIE) String activeClientId, @PathVariable Long id, @RequestBody Review review) {
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

package com.example.productreviewsapp.controllers.restControllers;

import com.example.productreviewsapp.models.Client;
import com.example.productreviewsapp.models.Review;
import com.example.productreviewsapp.models.SystemConstants;
import com.example.productreviewsapp.repositories.ClientRepository;
import com.example.productreviewsapp.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * RestController for API calls for the Review class.
 */
@RestController
@RequestMapping(value = "/api")
public class ReviewRESTController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Get review.
     *
     * @param id Long
     * @return Review
     */
    private Review getReview(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return review.get();
    }

    /**
     * Get client
     *
     * @param id Long
     * @return Client
     */
    private Client getClient(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return client.get();
    }

    /**
     * Get review mapping.
     *
     * @return Iterable<Review>
     */
    @GetMapping("/review")
    public Iterable<Review> getReviews() {
        return reviewRepository.findAll();
    }

    /**
     * Get review by id mapping.
     *
     * @param id Long
     * @return Review
     */
    @GetMapping(value = "/review/{id}", produces = "application/json")
    public Review getReviewById(@PathVariable("id") Long id) {
        return getReview(id);
    }

    /**
     * Post create review mapping.
     *
     * @param review Review
     * @return Review
     */
    @PostMapping(value = "/review", consumes = "application/json", produces = "application/json")
    public Review createReview(@RequestBody Review review) {
        reviewRepository.save(review);
        return review;
    }

    /**
     * Put product review mapping.
     *
     * @param activeClientId String
     * @param id             Long
     * @param review         Review
     * @return Review
     */
    @PutMapping(value = "/product/{id}/addReview", consumes = "application/json", produces = "application/json")
    public Review addReview(@CookieValue(value = SystemConstants.ACTIVE_CLIENT_ID_COOKIE) String activeClientId, @PathVariable Long id, @RequestBody Review review) {
        Client client = getClient(Long.parseLong(activeClientId));
        client.addReviewForProduct(id, review);
        clientRepository.save(client);
        return review;
    }

    /**
     * Put update review mapping.
     *
     * @param id        Long
     * @param newReview Review
     * @return Review
     */
    @PutMapping(value = "/review/{id}", consumes = "application/json", produces = "application/json")
    public Review updateReview(@PathVariable Long id, @RequestBody Review newReview) {
        Review review = getReview(id);
        review.setRating(newReview.getRating());
        if (newReview.getComment() != null) {
            newReview.setComment(newReview.getComment());
        }
        return reviewRepository.save(review);
    }

    /**
     * Delete review mapping.
     *
     * @param id Long
     * @return Review
     */
    @DeleteMapping(value = "/review/{id}")
    public Review deleteReview(@PathVariable Long id) {
        Review review = getReview(id);
        reviewRepository.deleteById(id);
        return review;
    }
}

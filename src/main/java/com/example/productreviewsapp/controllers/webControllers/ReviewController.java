package com.example.productreviewsapp.controllers.webControllers;

import com.example.productreviewsapp.models.Client;
import com.example.productreviewsapp.models.Product;
import com.example.productreviewsapp.models.Review;
import com.example.productreviewsapp.models.SystemConstants;
import com.example.productreviewsapp.repositories.ClientRepository;
import com.example.productreviewsapp.repositories.ProductRepository;
import com.example.productreviewsapp.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Review controller class.
 */
@Controller
public class ReviewController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Get product.
     *
     * @param id Long
     * @return Product
     */
    private Product getProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return product.get();
    }

    /**
     * Get client.
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
     * Get reviews mapping.
     *
     * @param model Model
     * @return String
     */
    @GetMapping(value = "/review", produces = "application/json")
    public String getReviews(Model model) {
        List<Review> reviewList = reviewRepository.findAll();
        model.addAttribute("ReviewList", reviewList);
        return "review";
    }

    /**
     * Get review by id mapping.
     *
     * @param id    Long
     * @param model Model
     * @return String
     */
    @GetMapping(value = "/review/{id}", produces = "application/json")
    public String getReviewById(@PathVariable("id") Long id, Model model) {
        Review review = getReview(id);
        clientRepository.findById(review.getClient().getId()).ifPresent(authorOfReview -> model.addAttribute("author", authorOfReview.getUsername()));
        model.addAttribute("review", review);
        return "review-page";
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
     * Post add review mapping.
     *
     * @param activeClientId String
     * @param reviewComment  String
     * @param productId      long
     * @param reviewRating   int
     * @param model          Model
     * @return String
     */
    @PostMapping(value = "/submitReview")
    public String addReview(@CookieValue(value = SystemConstants.ACTIVE_CLIENT_ID_COOKIE) String activeClientId, @RequestParam(value = "reviewComment") String reviewComment, @RequestParam(value = "productId") long productId, @RequestParam(value = "rating") int reviewRating, Model model) {
        Product product = getProduct(productId);
        Client client = getClient(Long.parseLong(activeClientId));

        Review review = new Review(reviewRating, reviewComment, product, client);
        productRepository.save(product);

        client.addReviewForProduct(productId, review);
        clientRepository.save(client);

        ArrayList<Review> reviewsForProduct = new ArrayList<>();
        for (Review fetchedReview : reviewRepository.findAll()) {
            if (Objects.equals(fetchedReview.getProduct().getId(), product.getId()))
                reviewsForProduct.add(fetchedReview);
        }

        model.addAttribute("reviews", reviewsForProduct);
        model.addAttribute("product", product);
        model.addAttribute("hasReview", client.hasReviewForProduct(productId));
        model.addAttribute("activeClient", client);
        return "product-page";
    }

    /**
     * Put updated review mapping.0
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

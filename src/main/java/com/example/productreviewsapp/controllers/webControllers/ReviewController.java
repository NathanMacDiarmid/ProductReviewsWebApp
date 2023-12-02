package com.example.productreviewsapp.controllers.webControllers;

import com.example.productreviewsapp.models.Client;
import com.example.productreviewsapp.models.Product;
import com.example.productreviewsapp.models.Review;
import com.example.productreviewsapp.models.SystemConstants;
import com.example.productreviewsapp.repositories.ClientRepository;
import com.example.productreviewsapp.repositories.ProductRepository;
import com.example.productreviewsapp.repositories.ReviewRepository;
import jakarta.servlet.http.HttpServletResponse;
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

@Controller
public class ReviewController {

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

    private Client getClient(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return client.get();
    }

    private Review getReview(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return review.get();
    }

    @GetMapping(value="/review", produces="application/json")
    public String getReviews(Model model) {
        List<Review> reviewList = reviewRepository.findAll();
        model.addAttribute("ReviewList", reviewList);
        return "review";
    }

    @GetMapping(value="/review/{id}", produces="application/json")
    public String getReviewById(@PathVariable("id") Long id, Model model, HttpServletResponse response) {
        Review review = getReview(id);
        clientRepository.findById(review.getClient().getId()).ifPresent(authorOfReview -> model.addAttribute("author", authorOfReview.getUsername()));
        model.addAttribute("review", review);
        return "review-page";
    }

    @PostMapping(value="/review", consumes="application/json", produces="application/json")
    public Review createReview(@RequestBody Review review) {
        reviewRepository.save(review);
        return review;
    }

    @PostMapping(value="/submitReview")
    public String addReview(@CookieValue(value = SystemConstants.ACTIVE_CLIENT_ID_COOKIE) String activeClientId, @RequestParam(value = "reviewComment") String reviewComment, @RequestParam(value = "productId") long productId, @RequestParam(value = "rating") int reviewRating, Model model) {
        Product product = getProduct(productId);
        Client client = getClient(Long.parseLong(activeClientId));

        Review review = new Review(reviewRating, reviewComment, product, client);
        productRepository.save(product);

        client.addReviewForProduct(productId, review);
        clientRepository.save(client);

        ArrayList<Review> reviewsForProduct = new ArrayList<>();
        for(Review fetchedReview : reviewRepository.findAll()) {
            if(Objects.equals(fetchedReview.getProduct().getId(), product.getId())) reviewsForProduct.add(fetchedReview);
        }

        model.addAttribute("reviews", reviewsForProduct);
        model.addAttribute("product", product);
        model.addAttribute("hasReview", client.hasReviewForProduct(productId));
        return "product-page";
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

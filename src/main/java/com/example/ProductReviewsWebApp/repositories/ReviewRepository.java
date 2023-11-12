package com.example.ProductReviewsWebApp.repositories;

import com.example.ProductReviewsWebApp.models.Review;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    @NonNull
    List<Review> findAll();
}

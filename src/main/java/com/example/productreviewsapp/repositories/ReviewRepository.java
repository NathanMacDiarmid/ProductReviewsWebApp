package com.example.productreviewsapp.repositories;

import com.example.productreviewsapp.models.Review;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    @NonNull
    List<Review> findAll();

//    List<Review> findByProduct(Product product);
}

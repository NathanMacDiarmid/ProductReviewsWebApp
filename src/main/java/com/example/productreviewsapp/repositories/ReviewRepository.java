package com.example.productreviewsapp.repositories;

import com.example.productreviewsapp.models.Review;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends CrudRepository<Review, Long> {
    @NonNull
    List<Review> findAll();

}

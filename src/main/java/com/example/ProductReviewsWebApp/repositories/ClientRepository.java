package com.example.ProductReviewsWebApp.repositories;

import com.example.ProductReviewsWebApp.models.Client;
import com.example.ProductReviewsWebApp.models.Review;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {
    @NonNull
    List<Client> findAll();

    Client findByUsername(String userName);

    boolean existsByUsername(String username);
}

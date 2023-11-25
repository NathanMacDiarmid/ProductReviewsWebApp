package com.example.ProductReviewsWebApp.repositories;

import com.example.ProductReviewsWebApp.models.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {
    @NonNull
    List<Client> findAll();

    Client findByUsername(String userName);

    boolean existsByUsername(String username);
}

package com.example.ProductReviewsWebApp.repositories;

import com.example.ProductReviewsWebApp.models.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {
    Client findByUsername(String userName);
}

package com.example.ProductReviewsWebApp.clients;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {
    Client findByUsername(String userName);
}

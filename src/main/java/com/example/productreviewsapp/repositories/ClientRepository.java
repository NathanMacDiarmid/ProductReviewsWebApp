package com.example.productreviewsapp.repositories;

import com.example.productreviewsapp.models.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Client repository.
 */
@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {
    @NonNull
    List<Client> findAll();

    Client findByUsername(String userName);

    boolean existsByUsername(String username);
}

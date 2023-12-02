package com.example.productreviewsapp.repositories;

import com.example.productreviewsapp.models.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    @NonNull
    List<Product> findAll();

}

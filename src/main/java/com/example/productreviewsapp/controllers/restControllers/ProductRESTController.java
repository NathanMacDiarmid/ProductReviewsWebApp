package com.example.productreviewsapp.controllers.restControllers;

import com.example.productreviewsapp.models.Product;
import com.example.productreviewsapp.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


/**
 * RestController for API calls for the Product class.
 */
@RestController
@RequestMapping(value = "/api")
public class ProductRESTController {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Get product.
     *
     * @param id Long
     * @return Product
     */
    private Product getProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return product.get();
    }

    /**
     * Get products mapping.
     *
     * @return Iterable<Product>
     */
    @GetMapping("/product")
    public Iterable<Product> getProducts() {
        return productRepository.findAll();
    }

    /**
     * Get product by id mapping.
     *
     * @param id Long
     * @return Product
     */
    @GetMapping(value = "/product/{id}", produces = "application/json")
    public Product getProductById(@PathVariable("id") Long id) {
        return getProduct(id);
    }

    /**
     * Post product mapping.
     *
     * @param product Product
     * @return Product
     */
    @PostMapping(value = "/product", consumes = "application/json", produces = "application/json")
    public Product createProduct(@RequestBody Product product) {
        productRepository.save(product);
        return product;
    }

    /**
     * Put product mapping.
     *
     * @param id         Long
     * @param newProduct Product
     * @return Product
     */
    @PutMapping(value = "/product/{id}", consumes = "application/json", produces = "application/json")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product newProduct) {
        Product product = getProduct(id);
        if (newProduct.getName() != null) {
            product.setName(newProduct.getName());
        }
        if (newProduct.getUrl() != null) {
            product.setUrl(newProduct.getUrl());
        }
        if (newProduct.getCategory() != null) {
            product.setCategory(newProduct.getCategory());
        }
        return productRepository.save(product);
    }

    /**
     * Delete product mapping.
     *
     * @param id Long
     * @return Product
     */
    @DeleteMapping(value = "/product/{id}")
    public Product deleteProduct(@PathVariable Long id) {
        Product product = getProduct(id);
        productRepository.deleteById(id);
        return product;
    }

}

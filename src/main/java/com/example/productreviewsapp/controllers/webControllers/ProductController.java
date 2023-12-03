package com.example.productreviewsapp.controllers.webControllers;

import com.example.productreviewsapp.models.Client;
import com.example.productreviewsapp.models.Product;
import com.example.productreviewsapp.models.Review;
import com.example.productreviewsapp.models.SystemConstants;
import com.example.productreviewsapp.repositories.ClientRepository;
import com.example.productreviewsapp.repositories.ProductRepository;
import com.example.productreviewsapp.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Client controller class.
 */
@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Get client.
     *
     * @param id Long
     * @return Client
     */
    private Client getClient(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return client.get();
    }

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
     * Get product mapping.
     *
     * @param model Model
     * @return String
     */
    @GetMapping(value = "/product")
    public String getProducts(Model model) {
        List<Product> productList = productRepository.findAll();
        model.addAttribute("ProductList", productList);
        return "product";
    }

    /**
     * Get product by id mapping.
     *
     * @param activeClientId String
     * @param id             Long
     * @param model          Model
     * @return String
     */
    @GetMapping(value = "/product/{id}", produces = "application/json")
    public String getProductById(@CookieValue(value = SystemConstants.ACTIVE_CLIENT_ID_COOKIE) String activeClientId, @PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) throw new NullPointerException("Was not able to find product with the passed ID");
        ArrayList<Review> reviewsForProduct = new ArrayList<>();

        for (Review review : reviewRepository.findAll()) {
            if (Objects.equals(review.getProduct().getId(), product.getId())) reviewsForProduct.add(review);
        }

        Client client = getClient(Long.parseLong(activeClientId));

        model.addAttribute("activeClient", client);
        model.addAttribute("reviews", reviewsForProduct);
        model.addAttribute("product", product);
        model.addAttribute("hasReview", client.hasReviewForProduct(id));
        return "product-page";
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
     * Put updated product mapping.
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

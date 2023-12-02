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

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ClientRepository clientRepository;

    private Client getClient(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return client.get();
    }

    private Product getProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return product.get();
    }

    @GetMapping(value="/product")
    public String getProducts(Model model) {
        List<Product> productList = productRepository.findAll();
        model.addAttribute("ProductList", productList);
        return "product";
    }

    @GetMapping(value="/product/{id}", produces="application/json")
    public String getProductById(@CookieValue(value = SystemConstants.ACTIVE_CLIENT_ID_COOKIE) String activeClientId, @PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) throw new NullPointerException("Was not able to find product with the passed ID");
        ArrayList<Review> reviewsForProduct = new ArrayList<>();

        for(Review review : reviewRepository.findAll()) {
            if(Objects.equals(review.getProduct().getId(), product.getId())) reviewsForProduct.add(review);
        }

        Client client = getClient(Long.parseLong(activeClientId));

        model.addAttribute("activeClient", client);
        model.addAttribute("reviews", reviewsForProduct);
        model.addAttribute("product", product);
        model.addAttribute("hasReview", client.hasReviewForProduct(id));
        return "product-page";
    }

    @PostMapping(value="/product", consumes="application/json", produces="application/json")
    public Product createProduct(@RequestBody Product product) {
        productRepository.save(product);
        return product;
    }

    @PutMapping(value="/product/{id}", consumes="application/json", produces="application/json")
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

    @DeleteMapping(value="/product/{id}")
    public Product deleteProduct(@PathVariable Long id) {
        Product product = getProduct(id);
        productRepository.deleteById(id);
        return product;
    }
}

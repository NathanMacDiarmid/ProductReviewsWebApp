package com.example.ProductReviewsWebApp.controllers;

import com.example.ProductReviewsWebApp.models.*;
import com.example.ProductReviewsWebApp.repositories.ClientRepository;
import com.example.ProductReviewsWebApp.repositories.ProductRepository;
import com.example.ProductReviewsWebApp.repositories.ReviewRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class ProductReviewsController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ClientRepository clientRepository;

    private String getAuthorOfReview(Review review) {
        List<Client> clientList = clientRepository.findAll();

        for (Client c : clientList) {
            if (c.hasReviewByReviewId(review.getId())) {
                return c.getUsername();
            }
        }

        return "";
    }

    private Product getProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return product.get();
    }

    private Review getReview(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return review.get();
    }

    private Client getClient(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return client.get();
    }

    private Client findClientById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return client.get();
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute FakeLoginRequest fakeLoginRequest, Model model) {
        model.addAttribute("loginRequest", new FakeLoginRequest(""));
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute FakeLoginRequest fakeLoginRequest, Model model, HttpServletResponse response) {
        model.addAttribute("loginRequest", fakeLoginRequest);

        if (clientRepository.existsByUsername(fakeLoginRequest.username())) {
            Client activeClient = clientRepository.findByUsername(fakeLoginRequest.username());
            Cookie activeClientID = new Cookie("activeClientID", activeClient.getId().toString());

            response.addCookie(activeClientID);
            return "home";
        }

        return "login";
    }

    @GetMapping("/logout")
    public String logoutAction(HttpServletResponse response) {
        Cookie deleteActiveClientId = new Cookie("activeClientID", null);
        response.addCookie(deleteActiveClientId);
        return "index";
    }

    @GetMapping(value="/product")
    public String getProducts(Model model) {
        List<Product> productList = productRepository.findAll();
        model.addAttribute("ProductList", productList);
        return "product";
    }

    @GetMapping(value="/review", produces="application/json")
    public String getReviews(Model model, HttpServletResponse response) {
        List<Review> reviewList = reviewRepository.findAll();
        model.addAttribute("ReviewList", reviewList);
        return "review";
    }

    @GetMapping(value = "/client", produces = "application/json")
    public String getClients(Model model) {
        List<Client> clientList = clientRepository.findAll();
        model.addAttribute("ClientList", clientList);
        return "client";
    }

    @GetMapping(value = "/myaccount", produces = "application/json")
    public String getMyAccount(@CookieValue(value = "activeClientID") String activeClientId, Model model) {
        Client client = getClient(Long.parseLong(activeClientId));
        model.addAttribute("activeClient", client);
        return "myAccount";
    }

    @GetMapping(value="/product/{id}", produces="application/json")
    public String getProductById(@CookieValue(value = "activeClientID") String activeClientId, @PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) throw new NullPointerException("Was not able to find product with the passed ID");
        ArrayList<Review> reviewsForProduct = new ArrayList<>();

        for(Review review : reviewRepository.findAll()) {
            if(Objects.equals(review.getProduct().getId(), product.getId())) reviewsForProduct.add(review);
        }

        // Client client = clientRepository.findByUsername("TestClient"); // TODO Replace with logged in client

        model.addAttribute("reviews", reviewsForProduct);
        model.addAttribute("product", product);
        // model.addAttribute("hasReview", client.hasReviewForProduct(id));
        return "product-page";
    }

    @GetMapping(value="/review/{id}", produces="application/json")
    public String getReviewById(@PathVariable("id") Long id, Model model, HttpServletResponse response) {
        Review review = getReview(id);

        clientRepository.findById(review.getAuthorId()).ifPresent(authorOfReview -> model.addAttribute("author", authorOfReview.getUsername()));

        model.addAttribute("review", review);
        return "review-page";
    }

    @GetMapping(value = "/client/{id}", produces = "application/json")
    public String getClientById(@PathVariable("id") Long id, @CookieValue(value = "activeClientID") String activeClientId, Model model) {
        Client client = this.findClientById(id);
        Client activeClient = this.findClientById(Long.parseLong(activeClientId));
        model.addAttribute("client", client);
        model.addAttribute("activeClient", activeClient);
        return "client-page";
    }

    @GetMapping(value = "/client/username/{name}", produces = "application/json")
    public String getClientByName(@PathVariable("name") String name, @CookieValue(value = "activeClientID") String activeClientId,  Model model) {
        Client client = clientRepository.findByUsername(name);
        Client activeClient = this.findClientById(Long.parseLong(activeClientId));
        model.addAttribute("client", client);
        model.addAttribute("activeClient", activeClient);
        return "client-page";
    }

    @GetMapping(value = "/client/clientsByJaccardDistance", produces = "application/json")
    public String getClientsByJaccardDistance(@CookieValue(value = "activeClientID") String activeClientId, Model model) {
        ClientsByJaccardDistanceSorted clients = new ClientsByJaccardDistanceSorted(clientRepository, Long.parseLong(activeClientId));
        List<Client> clientsSorted = clients.getClientsSortedByJaccardDistance();

        model.addAttribute("clientsByJaccardDistance", clients);
        model.addAttribute("listOfClientsByJaccardDistance", clientsSorted);
        return "clientsByJaccardDistance";
    }

    @PostMapping(value="/product", consumes="application/json", produces="application/json")
    public Product createProduct(@RequestBody Product product) {
        productRepository.save(product);
        return product;
    }

    @PostMapping(value="/review", consumes="application/json", produces="application/json")
    public Review createReview(@RequestBody Review review) {
        reviewRepository.save(review);
        return review;
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

    @PostMapping(value="/submitReview")
    public String addReview(@CookieValue(value = "activeClientID") String activeClientId, @RequestParam(value = "reviewComment") String reviewComment, @RequestParam(value = "productId") long productId, @RequestParam(value = "rating") int reviewRating, Model model) {
        Product product = getProduct(productId);
        Client client = getClient(Long.parseLong(activeClientId));

        Review review = new Review(reviewRating, reviewComment, product, client.getId());
        productRepository.save(product);

        client.addReviewForProduct(productId, review);
        clientRepository.save(client);

        ArrayList<Review> reviewsForProduct = new ArrayList<>();
        for(Review fetchedReview : reviewRepository.findAll()) {
            if(Objects.equals(fetchedReview.getProduct().getId(), product.getId())) reviewsForProduct.add(fetchedReview);
        }

        model.addAttribute("reviews", reviewsForProduct);
        model.addAttribute("product", product);
        model.addAttribute("hasReview", client.hasReviewForProduct(productId));
        return "product-page";
    }

    @PutMapping(value="/review/{id}", consumes="application/json", produces="application/json")
    public Review updateReview(@PathVariable Long id, @RequestBody Review newReview) {
        Review review = getReview(id);
        review.setRating(newReview.getRating());
        if (newReview.getComment() != null) {
            newReview.setComment(newReview.getComment());
        }
        return reviewRepository.save(review);
    }

    @DeleteMapping(value="/product/{id}")
    public Product deleteProduct(@PathVariable Long id) {
        Product product = getProduct(id);
        productRepository.deleteById(id);
        return product;
    }

    // This method is only for the sake of testing
    // Do not use this method to delete a review from a product
    // or else an error will be thrown
    @DeleteMapping(value="/review/{id}")
    public Review deleteReview(@PathVariable Long id) {
        Review review = getReview(id);
        reviewRepository.deleteById(id);
        return review;
    }
}

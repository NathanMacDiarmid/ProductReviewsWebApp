package com.example.ProductReviewsWebApp.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The Product class represents information about a product, including their url, name and category.
 */
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // The id of the product.

    private String name; // The address of the product.

    private String category; // The category of the product.

    private String description; // The description of the product.

    private String url; // The url of the product.

    private double averageRating; // The average ratings of the product.

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "reviewedProduct")
    private List<Review> reviews;

    /**
     * Creates a new instance of Product.
     */
    public Product() {
    }

    /**
     * Creates a new instance of product.
     *
     * @param url      The name of the url.
     * @param name     The name of the product.
     * @param category The category of the product.
     */
    public Product(String url, String name, String category) {
        this.url = url;
        this.name = name;
        this.description = "Product description";
        this.category = category;
        this.averageRating = 0;
        this.reviews = new ArrayList<>();
    }

    /**
     * Retrieves the ID of the product.
     *
     * @return The ID of the product.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the product.
     *
     * @param id The ID of the product.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the url of the product.
     *
     * @return The url of the product.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url of the product.
     *
     * @param url The url of the product.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Retrieves the name of the product.
     *
     * @return The name of the product.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the product.
     *
     * @param name The name of the product.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the category of the product.
     *
     * @return The category of the product.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Gets the product reviews
     * @return the ArrayList of the product reviews
     */
    public List<Review> getReviews() {
        return reviews;
    }

    /**
     * Sets the category of the product.
     *
     * @param category The category of the product.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Adds a review to the list of reviews
     * @param review the Review object
     */
    public void addReview(Review review) {
        reviews.add(review);
        updateAverageRating();
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    /**
     * Removes a review from the list of reviews
     * @param review the Review object
     */
    public void removeReview(Review review) {
        reviews.remove(review);
    }

    /**
     * Removes a review from the list of reviews
     * @param reviewIndex the int of the review index in the reviews ArrayList
     */
    public void removeReview(int reviewIndex) {
        reviews.remove(reviewIndex);
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public void updateAverageRating() {
        averageRating = reviews.stream().mapToDouble(Review::getRating).sum() / reviews.size();
    }

    /**
     * To string method for a product.
     *
     * @return A String representation of the product.
     */
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}

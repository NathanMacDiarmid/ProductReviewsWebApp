package com.example.ProductReviewsWebApp.models;

import jakarta.persistence.*;

/**
 * The Review class that contains all the information in a review
 */
@Entity
public class Review {
    @Id
    @GeneratedValue
    private long id;

    private int rating;

    private String description;

    private boolean forTesting;

    /**
     * Default constructor
     */
    public Review () {}

    /**
     * Constructor for the Review class
     * @param rating the int of the rating for the product
     * @param description the String of the review description
     */
    public Review(int rating, String description) {
        this.rating = rating;
        this.description = description;
    }

    /**
     * Gets the id of the review
     * @return the long id
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the rating of the product
     * @return the int of the product rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * Gets the description of the product review
     * @return the String of the product review description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the review id
     * @param id the long of the review id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Sets the rating of the product
     * @param rating the int of the product rating
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * Sets the product review description
     * @param description the String of the product review description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the review as a testing review only.
     */
    public void setForTesting() {
        forTesting = true;
    }

    /**
     * Check if review is for testing or not.
     * @return True if test review.
     */
    public boolean isForTesting() {
        return forTesting;
    }
}

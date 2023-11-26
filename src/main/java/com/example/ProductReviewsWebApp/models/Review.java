package com.example.ProductReviewsWebApp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The Review class that contains all the information in a review
 */
@Getter
@Setter
@Entity
public class Review {
    /**
     * -- GETTER --
     *  Gets the id of the review
     */
    @Id
    @GeneratedValue
    private long id;

    /**
     * -- GETTER --
     *  Gets the rating of the product
     */
    private int rating;

    /**
     * -- GETTER --
     *  Gets the comment of the product review
     */
    private String comment;

    /**
     * -- GETTER --
     *  Check if review is for testing or not.
     */
    private boolean forTesting;

    @ManyToOne
    private Product product;

    /**
     * Default constructor
     */
    public Review () {}

    /**
     * Constructor for the Review class
     * @param rating the int of the rating for the product
     * @param comment the String of the review comment
     */
    public Review(int rating, String comment, Product product) {
        this.rating = rating;
        this.comment = comment;
        this.product = product;
        this.product.updateAverageRating(rating);
    }

    /**
     * Set the review as a testing review only.
     */
    public void setForTesting() {
        forTesting = true;
    }

}

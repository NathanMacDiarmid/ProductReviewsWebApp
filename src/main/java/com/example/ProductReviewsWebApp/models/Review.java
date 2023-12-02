package com.example.ProductReviewsWebApp.models;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

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

    @ManyToOne
    private Product product;

    @ManyToOne
    private Client client;

    /**
     * Default constructor
     */
    public Review () {}

    /**
     * Constructor for the Review class
     * @param rating the int of the rating for the product
     * @param comment the String of the review comment
     */
    public Review(int rating, String comment, Product product, Client client) {
        this.rating = rating;
        this.comment = comment;
        this.product = product;
        this.client = client;
        this.product.updateAverageRating(rating);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return id == review.id && rating == review.rating && comment.equals(review.comment) && product.equals(review.product) && client.equals(review.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rating, comment, product, client);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", product=" + product +
                ", client=" + client +
                '}';
    }
}

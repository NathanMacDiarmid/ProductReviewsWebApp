package com.example.productreviewsapp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * The Review class that contains all the information in a review.
 */
@Getter
@Setter
@Entity
public class Review {
    /**
     * -- GETTER --
     * Gets the id of the review
     */
    @Id
    @GeneratedValue
    private long id;

    /**
     * -- GETTER --
     * Gets the rating of the product
     */
    private int rating;

    /**
     * -- GETTER --
     * Gets the comment of the product review
     */
    private String comment;

    /**
     * -- GETTER --
     * Gets the product of the product review
     */
    @ManyToOne
    private Product product;

    /**
     * -- GETTER --
     * Gets the client of the product review
     */
    @ManyToOne
    private Client client;

    /**
     * Default constructor
     */
    public Review() {
    }

    /**
     * Constructor for the Review class.
     *
     * @param rating  int
     * @param comment String
     * @param product Product
     * @param client  Client
     */
    public Review(int rating, String comment, Product product, Client client) {
        this.rating = rating;
        this.comment = comment;
        this.product = product;
        this.client = client;
        this.product.updateAverageRating(rating);
    }

    /**
     * equals method.
     *
     * @param o Object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return id == review.id &&
                rating == review.rating &&
                comment.equals(review.comment) &&
                product.equals(review.product) &&
                client.equals(review.client);
    }

    /**
     * hashCode method
     *
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, rating, comment, product, client);
    }

    /**
     * toString method.
     *
     * @return String
     */
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

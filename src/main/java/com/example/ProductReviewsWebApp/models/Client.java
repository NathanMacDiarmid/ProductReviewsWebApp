package com.example.ProductReviewsWebApp.models;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * The Client entity class containing the model logic of a typical client.
 */
@Entity
public class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    /**
     * The ID of the client.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The Username of the client.
     */
    @Column(unique = true)
    private String username;

    /**
     * The review for each product id the client has made.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private final List<Review> reviews;

    /**
     * The list of client's this client is following.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private final List<Client> following;

    /**
     * The follower count of the client, or how many user's follow it.
     */
    private int followerCount;

    /**
     * A condition to further lock follower count. True if follower count is safe to update. False otherwise.
     */
    private boolean canUpdateFollowerCount;

    /**
     * An empty constructor for a Client.
     */
    public Client() {
        this("", new ArrayList<>(), new ArrayList<>(), 0);
    }

    /**
     * Constructor for Client that allows a specified username.
     *
     * @param username String, the username of the Client.
     */
    public Client(String username) {
        this(username, new ArrayList<>(), new ArrayList<>(), 0);
    }

    /**
     * Constructor for Client that allows for all variables to be specified.
     *
     * @param username String, the username of the Client.
     * @param reviews List<Review>, the list of reviews.
     * @param following List<Client>, the users this user follows.
     * @param followerCount int, how many users follow this client.
     */
    public Client(String username, List<Review> reviews, List<Client> following, int followerCount) {
        this.username = username;
        this.reviews = reviews;
        this.following = following;
        this.followerCount = followerCount;
        this.canUpdateFollowerCount = true;
    }

    /* Main Functionality */

    /**
     * Add a review under the current user
     *
     * @param review Review, the review to add.
     */
    public void addReview(Review review) {
        reviews.add(review);
    }

    /**
     * Remove a review.
     * @param reviewId long, the reviewId for which review to remove.
     */
    public void removeReview(Long reviewId) {
        for (Review review : reviews) {
            if (review.getId() == reviewId) {
                reviews.remove(review);
                return;
            }
        }
    }

    /**
     * Get the follower count of the client.
     * @return int, the follower count.
     */
    public synchronized int getFollowerCount() {
        while (!canUpdateFollowerCount) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.error(e.toString());
            }
        }

        notifyAll();
        return followerCount;
    }

    /**
     * Increment follower count.
     */
    public synchronized void incrementFollowerCount() {
        while (!canUpdateFollowerCount) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.error(e.toString());
            }
        }

        followerCount++;
        notifyAll();
    }

    /**
     * Decrement follower count.
     */
    public synchronized void decrementFollowerCount() {
        while (!canUpdateFollowerCount) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.error(e.toString());
            }
        }

        followerCount = (followerCount > 0) ? followerCount - 1 : 0;
        notifyAll();
    }

    /**
     * Add a client to your following list and increment their follower count.
     *
     * @param clientToFollow Client, the client to follow.
     * @return True if following is successful, False otherwise.
     */
    public synchronized boolean followUser(Client clientToFollow) {
        if (following.contains(clientToFollow))
            return false;

        while (!canUpdateFollowerCount) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.error(e.toString());
                return false;
            }
        }

        clientToFollow.incrementFollowerCount();

        canUpdateFollowerCount = false;
        this.following.add(clientToFollow);
        canUpdateFollowerCount = true;
        notifyAll();
        return true;
    }

    /**
     * Unfollow a client and decrement their following count.
     *
     * @param clientToUnfollow Client, the client to unfollow.
     * @return True if unfollowing is successful, false otherwise.
     */
    public synchronized boolean unfollowUser(Client clientToUnfollow) {
        if (!following.contains(clientToUnfollow))
            return false;


        while (!canUpdateFollowerCount) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.error(e.toString());
                return false;
            }
        }

        clientToUnfollow.decrementFollowerCount();

        canUpdateFollowerCount = false;
        this.following.remove(clientToUnfollow);
        canUpdateFollowerCount = true;
        notifyAll();
        return true;
    }
// TODO Must reimplement
//    /**
//     * Get the Jaccard Distance of two users. <a href="https://www.learndatasci.com/glossary/jaccard-similarity/">...</a>
//     *
//     * @param clientToCompare Client, the client to calculate with.
//     * @return The similarity score of the two clients. 1 -> identical reviews, 0 -> completely unique.
//     */
//    public double getJaccardDistanceWithUser(Client clientToCompare) {
//        int similarReviews = 0;
//        if (this.getAllReviews().isEmpty() || clientToCompare.getAllReviews().isEmpty())
//            return 0;
//        for (Long productID : this.reviews.keySet()) {
//            if (clientToCompare.hasReviewForProduct(productID)) {
//                similarReviews = (clientToCompare.getReviewForProduct(productID).getRating() == this.reviews.get(productID).getRating()) ? similarReviews + 1 : similarReviews;
//            }
//        }
//
//        int unionLength = this.reviews.size() + clientToCompare.getAllReviews().size() - similarReviews;
//
//        BigDecimal jaccardDistance = new BigDecimal(similarReviews);
//
//        return jaccardDistance.divide(BigDecimal.valueOf(unionLength), 2, RoundingMode.UP).doubleValue();
//    }

    /* Basic Getters and Setters */

    /**
     * Set ID
     * @param id Long, the id.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the id of the client.
     * @return Long, the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the client's following list.
     * @return List, the following list.
     */
    public List<Client> getFollowingList() {
        return following;
    }

    /**
     * Get the client's username.
     * @return String, the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get all reviews
     * @return List<Review>, the reviews.
     */
    public List<Review> getAllReviews() {
        return reviews;
    }

    // TODO reimplement? Do we need this?
//    /**
//     * Get a review for a specified product.
//     *
//     * @param productID Long, the product's id.
//     * @return Review, the review.
//     */
//    public Review getReviewForProduct(Long productID) {
//        return reviews.get(productID);
//    }

    // TODO reimplement? Do we need this?
//    /**
//     * Get if client has written a review of a specified product.
//     *
//     * @param productID Long, the product's id.
//     * @return True, if the review exists, False otherwise.
//     */
//    public boolean hasReviewForProduct(Long productID) {
//        return reviews.containsKey(productID);
//    }

    /**
     * Set the username of the client.
     * @param username String, the new username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client client)) return false;
        return getId().equals(client.getId());
    }

    /**
     * A Basic To String for a String representation of a Client.
     * @return String, the client's representation.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", reviews=" + reviews +
                ", following=" + following +
                ", followerCount=" + followerCount +
                '}';
    }
}

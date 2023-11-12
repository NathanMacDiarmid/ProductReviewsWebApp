package com.example.ProductReviewsWebApp.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * The Client entity class containing the model logic of a typical client.
 */
@Entity
public class Client {

    /**
     * The ID of the client.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The Username of the client.
     */
    private String username;

    /**
     * The review for each product id the client has made.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private final Map<Long, Review> reviews;

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
        this("", new HashMap<>(), new ArrayList<>(), 0);
    }

    /**
     * Constructor for Client that allows a specified username.
     *
     * @param username String, the username of the Client.
     */
    public Client(String username) {
        this(username, new HashMap<>(), new ArrayList<>(), 0);
    }

    /**
     * Constructor for Client that allows for all variables to be specified.
     *
     * @param username String, the username of the Client.
     * @param reviews Map<Long, Review>, the product, review pairs.
     * @param following List<Client>, the users this user follows.
     * @param followerCount int, how many users follow this client.
     */
    public Client(String username, Map<Long, Review> reviews, List<Client> following, int followerCount) {
        this.username = username;
        this.reviews = reviews;
        this.following = following;
        this.followerCount = followerCount;
        this.canUpdateFollowerCount = true;
    }

    /* Main Functionality */

    /**
     * Add a review to a Product.
     *
     * @param productID Long, the product id to add to.
     * @param review Review, the review to add.
     */
    public void addReviewForProduct(Long productID, Review review) {
        reviews.put(productID, review);
    }

    /**
     * Remove a product review.
     * @param productID int, the productID for which review to remove.
     */
    public void removeReviewForProduct(Long productID) {
        reviews.remove(productID);
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
                e.printStackTrace();
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
                e.printStackTrace();
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
                e.printStackTrace();
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
                e.printStackTrace();
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
                e.printStackTrace();
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

    /**
     * Get the Jaccard Distance of two users. https://www.learndatasci.com/glossary/jaccard-similarity/
     *
     * @param clientToCompare Client, the client to calculate with.
     * @return The similarity score of the two clients. 1 -> identical reviews, 0 -> completely unique.
     */
    public double getJaccardDistanceWithUser(Client clientToCompare) {
        int similarReviews = 0;
        if (this.getAllReviews().isEmpty() || clientToCompare.getAllReviews().isEmpty())
            return 0;
        for (Long productID : this.reviews.keySet()) {
            if (clientToCompare.hasReviewForProduct(productID)) {
                similarReviews = (clientToCompare.getReviewForProduct(productID).getRating() == this.reviews.get(productID).getRating()) ? similarReviews + 1 : similarReviews;
            }
        }

        int unionLength = this.reviews.size() + clientToCompare.getAllReviews().size() - similarReviews;

        BigDecimal jaccardDistance = new BigDecimal(similarReviews);

        return jaccardDistance.divide(BigDecimal.valueOf(unionLength), 2, RoundingMode.UP).doubleValue();
    }

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
     * Get all product id, review pairs.
     * @return Map<Long, Review>, the pairs.
     */
    public Map<Long, Review> getAllReviews() {
        return reviews;
    }

    /**
     * Get a review for a specified product.
     *
     * @param productID Long, the product's id.
     * @return Review, the review.
     */
    public Review getReviewForProduct(Long productID) {
        return reviews.get(productID);
    }

    /**
     * Get if client has written a review of a specified product.
     *
     * @param productID Long, the product's id.
     * @return True, if the review exists, False otherwise.
     */
    public boolean hasReviewForProduct(Long productID) {
        return reviews.containsKey(productID);
    }

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

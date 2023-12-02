package com.example.productreviewsapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * The Client entity class containing the model logic of a typical client.
 */
@Getter
@Setter
@Entity
public class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    /**
     * The ID of the client.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The Username of the client.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * The review for each product id the client has made.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private final Map<Long, Review> reviews;

    /**
     * The list of client's this client is following.
     */
    @JsonIgnore
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, targetEntity = Client.class)
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
     */
    public synchronized void followUser(Client clientToFollow) {
        if (following.contains(clientToFollow))
            return;

        while (!canUpdateFollowerCount) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.error(e.toString());
                return;
            }
        }
        canUpdateFollowerCount = false;
        clientToFollow.incrementFollowerCount();
        this.following.add(clientToFollow);
        canUpdateFollowerCount = true;
        notifyAll();
    }

    /**
     * Unfollow a client and decrement their following count.
     *
     * @param clientToUnfollow Client, the client to unfollow.
     */
    public synchronized void unfollowUser(Client clientToUnfollow) {
        if (!following.contains(clientToUnfollow))
            return;


        while (!canUpdateFollowerCount) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.error(e.toString());
                return;
            }
        }
        canUpdateFollowerCount = false;
        clientToUnfollow.decrementFollowerCount();
        this.following.remove(clientToUnfollow);
        canUpdateFollowerCount = true;
        notifyAll();
    }

    /**
     * Check if this user is following another given user.
     *
     * @param clientToCompare The given user.
     * @return True -> user is following given user, False -> user is not following given user.
     */
    public boolean isFollowing(Client clientToCompare) {
        return following.contains(clientToCompare);
    }

    /**
     * Get the Jaccard Distance of two users. <a href="https://www.learndatasci.com/glossary/jaccard-similarity/">...</a>
     *
     * @param clientToCompare Client, the client to calculate with.
     * @return The similarity score of the two clients. 1 -> identical reviews, 0 -> completely unique.
     */
    public double getJaccardDistanceWithUser(Client clientToCompare) {
        int similarReviews = 0;
        if (this.getReviews().isEmpty() || clientToCompare.getReviews().isEmpty())
            return 0;
        for (Long productID : this.reviews.keySet()) {
            if (clientToCompare.hasReviewForProduct(productID)) {
                similarReviews = (clientToCompare.getReviewForProduct(productID).getRating() == this.reviews.get(productID).getRating()) ? similarReviews + 1 : similarReviews;
            }
        }

        int unionLength = this.reviews.size() + clientToCompare.getReviews().size() - similarReviews;

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
     * Application of Dijkstra's algorithm used to find the relatedness between clients based on following network.
     * A breadth-first search to find the shortest path between the current client and the destination client.
     * @param destination, the destination client
     * @return int, value of the degree of separation
     */
    public int getDegreesOfSeparation(Client destination) {
        // if the client is self, or no following
        if (this == destination || this.following.isEmpty()) {
            return 0;
        }

        // queue to store next clients to visit
        Queue<Client> queue = new LinkedList<>();
        // set to store the visited clients
        Set<Client> visited = new HashSet<>();
        // map to store each client and its corresponding distance from the source client
        Map<Client, Integer> distances = new HashMap<>();

        // initialise with self
        queue.add(this);
        visited.add(this);
        distances.put(this, 0);

        while (!queue.isEmpty()) {
            Client currClient = queue.poll();
            int currDistance = distances.get(currClient);

            for (Client following : currClient.getFollowing()) {
                // checking for following client existence
                if (!visited.contains(following)) {
                    queue.add(following);
                    visited.add(following);
                    distances.put(following, currDistance + 1);
                    // following client reached the destination client
                    if (following.equals(destination)) {
                        return distances.get(following);
                    }
                }
            }
        }
        return 0; // clients are not connected
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
                // ", reviews=" + reviews +
                // ", following=" + following +
                ", followerCount=" + followerCount +
                '}';
    }
}
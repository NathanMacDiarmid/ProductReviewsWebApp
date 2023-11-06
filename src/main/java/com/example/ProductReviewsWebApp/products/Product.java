package com.example.ProductReviewsWebApp.products;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * The Product class represents information about a product, including their url, name and category.
 */
@Entity
public class Product {

    @Id
    @GeneratedValue()
    private Long id; // The id of the product.

    private String url; // The name of the product.

    private String name; // The address of the product.

    private String category; // The category of the product.

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
        this.category = category;
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
     * Sets the category of the product.
     *
     * @param category The category of the product.
     */
    public void setCategory(String category) {
        this.category = category;
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

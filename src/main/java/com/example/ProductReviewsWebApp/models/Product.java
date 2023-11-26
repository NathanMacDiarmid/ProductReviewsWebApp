package com.example.ProductReviewsWebApp.models;

import jakarta.persistence.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static org.json.simple.parser.JSONParser.*;

/**
 * The Product class represents information about a product, including their url, name and category.
 */
@Getter
@Setter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id; // The id of the product.

    private String name; // The address of the product.

    private Category category; // The category of the product.

    private String description; // The description of the product.

    private String url; // The url of the product.

    private String image;

    private double averageRating; // The average ratings of the product.

    private int numOfReviews;

    static JSONParser parser = new JSONParser();

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
    public Product(String url, String name, Category category) {
        this.url = url;
        this.name = name;
        this.description = "Product description";
        this.category = category;
        this.averageRating = 0;
        this.numOfReviews = 0;
    }

    public Product(Category category, String name, String image, String description, String url) {
        this.category = category;
        this.name = name;
        this.image = image;
        this.description = description;
        this.url = url;
        this.averageRating = 0;
        this.numOfReviews = 0;
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
     * Sets the url of the product.
     *
     * @param url The url of the product.
     */
    public void setUrl(String url) {
        this.url = url;
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
     * Sets the category of the product.
     *
     * @param category The category of the product.
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    public void updateAverageRating(int rating) {
        double totalRating = this.averageRating * this.numOfReviews;
        this.numOfReviews++;
        totalRating += rating;
        this.averageRating = totalRating / numOfReviews;
    }

    public static ArrayList<Product> createProductsFromJSON(String filename) {
        ArrayList<Product> products = new ArrayList<>();
        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader(filename));
            Product product;
            for (Object o : a) {
                JSONObject item = (JSONObject) o;
                String category = (String) item.get("category");
                String name = (String) item.get("name");
                String image = (String) item.get("image");
                String description = (String) item.get("description");
                String url = (String) item.get("url");
                product = new Product(Category.valueOf(category.toUpperCase()), name, image, description, url);
                products.add(product);
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        return products;
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

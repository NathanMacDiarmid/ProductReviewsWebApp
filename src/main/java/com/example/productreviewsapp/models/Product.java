package com.example.productreviewsapp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The Product class represents information about a product, including their url, name and category.
 */
@Getter
@Setter
@Entity
public class Product {

    /**
     * -- GETTER --
     * Gets the id of the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /**
     * -- GETTER --
     * Gets the name of the product.
     */
    private String name;

    /**
     * -- GETTER --
     * Gets the category of the product.
     */
    private Category category;


    /**
     * -- GETTER --
     * Gets the description of the product.
     */
    private String description;

    /**
     * -- GETTER --
     * Gets the url of the product.
     */
    private String url;

    /**
     * -- GETTER --
     * Gets the image of the product.
     */
    private String image;

    /**
     * -- GETTER --
     * Gets the average rating of the product.
     */
    private double averageRating;

    /**
     * -- GETTER --
     * Gets the number of reviews of the product.
     */
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

    /**
     * Constructor for Product.
     *
     * @param category    Category
     * @param name        String
     * @param image       String
     * @param description String
     * @param url         String
     */
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

    /**
     * Update the averate rating of the product.
     *
     * @param rating int
     */
    public void updateAverageRating(int rating) {
        double totalRating = this.averageRating * this.numOfReviews;
        this.numOfReviews++;
        totalRating += rating;
        this.averageRating = Math.round((totalRating / numOfReviews) * 100.0) / 100.0;
    }

    /**
     * Creates a list of products from a JSON file.0
     *
     * @param filename String
     * @return ArrayList<Product>
     */
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
        Product product = (Product) o;
        return  Double.compare(averageRating, product.averageRating) == 0 &&
                numOfReviews == product.numOfReviews &&
                Objects.equals(id, product.id) && Objects.equals(name, product.name) &&
                category == product.category && Objects.equals(description, product.description) &&
                Objects.equals(url, product.url) && Objects.equals(image, product.image);
    }

    /**
     * hashCode method.
     *
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, category, description, url, image, averageRating, numOfReviews);
    }

}

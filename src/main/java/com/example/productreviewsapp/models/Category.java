package com.example.productreviewsapp.models;

import lombok.Getter;

@Getter
public enum Category {

    BOOK("book"),
    ELECTRONIC("electronic"),
    GAME("game"),
    MOVIE("movie"),
    MUSIC("music"),
    FOOD("food"),
    PLANT("plant");

    private final String name;
    Category(String name) {
        this.name = name;
    }

}

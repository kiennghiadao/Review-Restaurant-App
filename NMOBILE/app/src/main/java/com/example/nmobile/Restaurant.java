package com.example.nmobile;

public class Restaurant {
    private int id;
    private String name;
    private String location;
    private String type;
    private String details;
    private float rating;
    private int imageResId;

    // Constructor
    public Restaurant(int id, String name, String location, String type, String details, float rating, int imageResId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.type = type;
        this.details = details;
        this.rating = rating;
        this.imageResId = imageResId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}

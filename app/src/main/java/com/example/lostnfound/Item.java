package com.example.lostnfound;

import java.io.Serializable;

public class Item implements Serializable {
    private int id;
    private String type;
    private String category;
    private String imageUri;
    private String date;
    private String name;
    private String phone;
    private String description;
    private String location;
    private double latitude;
    private double longitude;

    public Item() {
    }

    public Item(int id, String type, String category, String imageUri, String date, String name, String phone,
            String description, String location, double latitude, double longitude) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.imageUri = imageUri;
        this.date = date;
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

package com.buihuuduy.btl_android.entity;

public class BookEntity {
    private int id;
    private String name;
    private String author;
    private double price;
    private String imageUrl;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // Constructor, getters, và setters
    public BookEntity(int id, String name, String author, double price, String imageUrl, int status) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public BookEntity() {
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAuthor() { return author; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}

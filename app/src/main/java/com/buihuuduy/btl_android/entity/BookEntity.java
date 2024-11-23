package com.buihuuduy.btl_android.entity;

public class BookEntity {
    private int id;
    private String name;
    private String description;
    private double price;
    private String author;

    public BookEntity(int id, String name, String description, double price, String author) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.author = author;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getAuthor() { return author; }
}

package com.buihuuduy.btl_android.entity;

import androidx.annotation.NonNull;
import java.time.LocalDate;

public class BookEntity
{
    private Integer id;
    private String name;
    private String description;
    private String content;
    private Integer price;
    private Integer status;
    private Integer categoryId;
    private Integer userId;
    private LocalDate createdAt;
    private String imagePath;

    // DTO
    private String userName;
    private String userEmail;
    private String categoryName;

    public BookEntity( String name, String description, String content, Integer price, Integer status, Integer categoryId, Integer userId, LocalDate createdAt, String imagePath) {
        this.name = name;
        this.description = description;
        this.content = content;
        this.price = price;
        this.status = status;
        this.categoryId = categoryId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.imagePath = imagePath;
    }

    public BookEntity(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @NonNull
    @Override
    public String toString() {
        return "BookEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", content='" + content + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", categoryId=" + categoryId +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", imagePath='" + imagePath + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}

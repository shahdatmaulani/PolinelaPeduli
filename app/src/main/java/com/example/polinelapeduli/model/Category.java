package com.example.polinelapeduli.model;

import org.jetbrains.annotations.NotNull;

public class Category {
    private int categoryId;
    private String name;

    // Constructor, Getters, and Setters
    public Category() {}

    public Category(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    // Getters and setters for all fields...
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    @NotNull
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                '}';
    }
}


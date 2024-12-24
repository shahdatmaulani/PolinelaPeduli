package com.example.polinelapeduli.model;

import com.example.polinelapeduli.utils.Enum.EStatus;

import org.jetbrains.annotations.NotNull;

public class Donation {
    private int donationId;
    private String name;
    private String description;
    private int categoryId;
    private String categoryName; // Nama kategori (baru)
    private int target;
    private String image;
    private EStatus status;
    private boolean isActive = true;
    private String createdAt;
    private String updatedAt;

    public Donation() {}

    public Donation(int donationId, String name, String description, int categoryId, int target, String image, EStatus status, boolean isActive, String createdAt, String updatedAt) {
        this.donationId = donationId;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.target = target;
        this.image = image;
        this.status = status;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getDonationId() {
        return donationId;
    }

    public void setDonationId(int donationId) {
        this.donationId = donationId;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    @NotNull
    public String toString() {
        return "Donation{" +
                "donationId=" + donationId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", target=" + target +
                ", image='" + image + '\'' +
                ", status=" + status +
                ", isActive=" + isActive +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
